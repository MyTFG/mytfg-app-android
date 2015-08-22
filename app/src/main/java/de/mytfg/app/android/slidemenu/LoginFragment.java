package de.mytfg.app.android.slidemenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.slidemenu.items.Navigation;

public class LoginFragment extends AbstractFragment {
    View loginview;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loginview = inflater.inflate(R.layout.login_layout, container, false);
        preferences = loginview.getContext().getSharedPreferences(getString(R.string.sharedpref_settings), Context.MODE_MULTI_PROCESS);
        prefEditor = preferences.edit();

        Long currentTimestamp = System.currentTimeMillis();
        long tokenTimeout = preferences.getLong(getString(R.string.settings_login_timeout), 0) * 1000;

        if (currentTimestamp < tokenTimeout) {
            // Already logged in
            MainActivity.navigation.navigate(Navigation.ItemNames.START);
            return null;
        }

        Button loginButton = (Button) loginview.findViewById(R.id.button_dologin);

        ((EditText)loginview.findViewById(R.id.edit_username)).setText(preferences.getString(getString(R.string.settings_login_username), ""));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v);
            }
        });

        return loginview;
    }

    public void doLogin(View view) {
        final String userText = ((EditText)getView().findViewById(R.id.edit_username)).getText().toString();
        String pwText = ((EditText) getView().findViewById(R.id.edit_password)).getText().toString();

        if (userText.length() == 0 || pwText.length() == 0) {
            Toast toast = Toast.makeText(getView().getContext(), "Bitte Nutzername und Passwort eingeben", Toast.LENGTH_LONG);
            toast.show();
        } else {
            ApiParams params = new ApiParams();
            params.addParam("user", userText);
            params.addParam("password", pwText);
            params.addParam("device", Settings.Secure.getString(getView().getContext().getContentResolver(), Settings.Secure.ANDROID_ID));

            MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
                @Override
                public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                    Toast toast;
                    if (success) {
                        if (result != null) {
                            try {
                                toast = Toast.makeText(getView().getContext(), "Login erfolgreich", Toast.LENGTH_LONG);
                                String token = result.getString("token");
                                long timeout = Long.parseLong(result.getString("tokentimeout"));
                                int userid = Integer.parseInt(result.getString("loginID"));
                                prefEditor.putString(getString(R.string.settings_login_username), userText);
                                prefEditor.putString(getString(R.string.settings_login_token), token);
                                prefEditor.putInt(getString(R.string.settings_login_userid), userid);
                                prefEditor.putLong(getString(R.string.settings_login_timeout), timeout);
                                prefEditor.commit();
                                MyTFG.refreshPrefs();
                            } catch (JSONException ex) {
                                toast = Toast.makeText(getView().getContext(), "Login erfolgreich, aber Fehler beim Parsen: " + ex.getMessage() , Toast.LENGTH_LONG);
                            } catch (Exception ex) {
                                toast = Toast.makeText(getView().getContext(), "Login erfolgreich, Integer Fehler", Toast.LENGTH_LONG);
                            }
                        } else {
                            toast = Toast.makeText(getView().getContext(), "Login erfolgreich, aber null", Toast.LENGTH_LONG);
                        }


                    } else {
                        toast = Toast.makeText(getView().getContext(), "Login fehlgeschlagen: " + responseCode, Toast.LENGTH_LONG);
                    }
                    toast.show();
                }
            };

            MytfgApi.call("ajax_auth_login", params, callback);
        }
    }


}
