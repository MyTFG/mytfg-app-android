package de.mytfg.app.android.modules.settings;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by lennart on 25-Aug-15.
 */
public abstract class SettingsItem {
    /**
     * Creates an Item to add to the Settings Layout.
     * @return A View to add to the Settings Layout.
     */
    public abstract View createItem();

    protected void setLayout(View child, int margin, int padding) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(margin, margin, margin, margin);
        child.setLayoutParams(layoutParams);

        child.setPadding(padding, padding, padding, padding);
    }

    protected void setLayout(View child) {
        this.setLayout(child, 10, 8);
    }
}
