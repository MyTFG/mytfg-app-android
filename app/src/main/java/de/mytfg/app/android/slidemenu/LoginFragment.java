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

import org.json.JSONObject;

import de.mytfg.app.android.R;
import de.mytfg.app.android.api.ApiParams;
import de.mytfg.app.android.api.MytfgApi;

public class LoginFragment extends Fragment {
    View loginview;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loginview = inflater.inflate(R.layout.login_layout, container, false);
        preferences = getView().getContext().getSharedPreferences(getString(R.string.sharedpref_settings), Context.MODE_MULTI_PROCESS);
        prefEditor = preferences.edit();

        Button loginButton = (Button) loginview.findViewById(R.id.button_dologin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v);
            }
        });

        return loginview;
    }

    public void doLogin(View view) {
        EditText userText = (EditText) getView().findViewById(R.id.edit_username);
        EditText pwText = (EditText) getView().findViewById(R.id.edit_password);

        if (userText.getText().length() == 0 || pwText.getText().length() == 0) {
            Toast toast = Toast.makeText(getView().getContext(), "Bitte Nutzername und Passwort eingeben", Toast.LENGTH_LONG);
            toast.show();
        } else {
            ApiParams params = new ApiParams();
            params.addParam("user", userText.getText().toString());
            params.addParam("password", pwText.getText().toString());
            params.addParam("device", Settings.Secure.getString(getView().getContext().getContentResolver(), Settings.Secure.ANDROID_ID));

            MytfgApi.ApiCallback callback = new MytfgApi.ApiCallback() {
                @Override
                public void callback(boolean success, JSONObject result, int responseCode, String resultStr) {
                    Toast toast;
                    if (success) {
                        toast = Toast.makeText(getView().getContext(), "Login erfolgreich: " + resultStr, Toast.LENGTH_LONG);
                        // TODO
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
