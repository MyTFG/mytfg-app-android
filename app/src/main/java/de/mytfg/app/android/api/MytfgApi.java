package de.mytfg.app.android.api;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.slidemenu.MainActivity;

/**
 * This class is an abstract wrapper to access the MyTFG API.
 * It allows to call API functions by name with given parameters.
 */
public class MytfgApi {
    /**
     * Calls an API function with given parameters. Calls the given callback function when request
     * finished.
     * @param apiFunction The path (name) of the API-function to call.
     * @param params Parameters to pass to the function.
     * @param callback Function to call when request finished (or timed out).
     */
    public static void call(String apiFunction, ApiParams params, ApiCallback callback) {
        if (params == null) {
            // Will not handle null params.
            callback.callback(false, null, -1, null);
        } else {
            params.doLogin();
            MyTFG.setLoadingBarVisble(true);
            new MytfgApi.RequestTask(apiFunction, params, callback).execute("");
        }
    }

    /**
     * Calls an API function without parameters. Calls the given callback function when request
     * finished.
     * @param apiFunction The path (name) of the API-function to call.
     * @param callback Function to call when request finished (or timed out).
     */
    public static void call(String apiFunction, ApiCallback callback) {
        call(apiFunction, new ApiParams(), callback);
    }

    public interface ApiCallback {
        /**
         * Called by the call-Method when request finished.
         * @param success True iff the request was successful.
         * @param result Data returned by the API as JSON.
         * @param responseCode The HTTP Response Code.
         * @param resultStr The returned Data as Plain String.
         */
        void callback(boolean success, JSONObject result, int responseCode,
                             String resultStr);
    }

    private static class RequestTask extends AsyncTask<String, String, String> {
        private String apiFunction;
        private ApiParams parameters;
        private ApiCallback callback;
        private static int activeCalls = 0;

        private String baseURL = "https://mytfg.de/";
        private String urlExtension = ".x";

        private int responseCode = -1;

        private RequestTask(String apiFunction, ApiParams params, ApiCallback callback) {
            this.apiFunction = apiFunction;
            this.parameters = params;
            this.callback = callback;
        }

        private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected String doInBackground(String... useless) {
            activeCalls++;
            try {
                URL url = new URL(baseURL + apiFunction + urlExtension);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(parameters.getMap()));
                writer.flush();
                writer.close();
                os.close();
                responseCode = connection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String response = "";
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    return response;
                } else {
                    return null;
                }

            } catch (MalformedURLException ex) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            activeCalls--;

            if (activeCalls <= 0) {
                MyTFG.setLoadingBarVisble(false);
            }

            if (result == null) {
                try {
                    JSONObject obj = new JSONObject("");
                    callback.callback(false, obj, responseCode, null);
                } catch (Exception ex) {
                    callback.callback(false, null, responseCode, null);
                }
            } else {
                try {
                    JSONObject obj = new JSONObject(result);
                    boolean status = (obj.getString("status").equals("1"));
                    callback.callback(status, obj, responseCode, result);
                } catch (Exception ex) {
                    Log.e("API", "Could not parse JSON result: " + ex.getMessage());
                    ex.printStackTrace();
                    callback.callback(false, null, responseCode, result);
                }
            }
        }
    }
}
