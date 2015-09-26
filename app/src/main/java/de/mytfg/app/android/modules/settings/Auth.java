package de.mytfg.app.android.modules.settings;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.utils.TimeUtils;

/**
 * Authentication Settings Item.
 */
public class Auth extends SettingsItem {
    @Override
    public View createItem() {
        LinearLayout ll = new LinearLayout(MainActivity.context);
        ll.setOrientation(LinearLayout.VERTICAL);
        setLayout(ll, 18, 20);
        ll.setBackgroundColor(MyTFG.color(R.color.blue_accent));

        if (MyTFG.isLoggedIn()) {
            return this.logout(ll);
        } else {
            return this.login(ll);
        }
    }

    private View logout(LinearLayout ll) {

        TextView tw = new TextView(MainActivity.context);
        tw.setText(MyTFG.string(R.string.setting_auth_logout_title));
        tw.setTextColor(MyTFG.color(R.color.white));
        tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyTFG.dimension(R.dimen.mytfg_title));

        ll.addView(tw);

        tw = new TextView(MainActivity.context);
        tw.setText(MyTFG.string(R.string.setting_auth_logout_user)
                + ":\n  " + MyTFG.getUsername());
        tw.setTextColor(MyTFG.color(R.color.white));
        tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyTFG.dimension(R.dimen.mytfg_text));
        ll.addView(tw);

        tw = new TextView(MainActivity.context);
        tw.setText(MyTFG.string(R.string.setting_auth_logout_timeout)
                + ":\n  " + TimeUtils.getDateStringShort(MyTFG.getTokenTimeout() / 1000) + "\n");
        tw.setTextColor(MyTFG.color(R.color.white));
        tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyTFG.dimension(R.dimen.mytfg_text));
        ll.addView(tw);


        Button btn = new Button(MainActivity.context);
        btn.setText(MyTFG.string(R.string.setting_auth_logout_button));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTFG.logout();
                MainActivity.navigation.navigate(Navigation.ItemNames.SETTINGS);
            }
        });

        ll.addView(btn);

        return ll;
    }

    private View login(LinearLayout ll) {
        TextView tw = new TextView(MainActivity.context);
        tw.setText(MyTFG.string(R.string.setting_auth_login_title) + "\n");
        tw.setTextColor(MyTFG.color(R.color.white));
        tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyTFG.dimension(R.dimen.mytfg_title));

        ll.addView(tw);

        Button btn = new Button(MainActivity.context);
        btn.setText(MyTFG.string(R.string.setting_auth_login_button));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navigation.navigate(Navigation.ItemNames.LOGIN, new Bundle(), Navigation.Transition.SLIDE, true);
            }
        });

        ll.addView(btn);

        return ll;
    }
}
