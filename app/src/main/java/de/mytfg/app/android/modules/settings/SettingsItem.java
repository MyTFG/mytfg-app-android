package de.mytfg.app.android.modules.settings;

import android.view.View;

/**
 * Created by lennart on 25-Aug-15.
 */
public abstract class SettingsItem {
    /**
     * Creates an Item to add to the Settings Layout.
     * @return A View to add to the Settings Layout.
     */
    public abstract View createItem();
}
