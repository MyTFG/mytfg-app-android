package de.mytfg.app.android.modulemanager;

import de.mytfg.app.android.modules.settings.Settings;

/**
 * A ModuleFactory to create Modules by ENUM.
 * Must be edited for every new module.
 */
public class ModuleFactory {
    public static Module createModule(Modules module) {
        switch (module) {
            case NOTIFICATIONS:
                // TODO: Add Notification module
                ModuleFactory.error();
                break;
            case SETTINGS:
                return new Settings();
            case TERMINAL:
                // TODO: Add Terminal module
                ModuleFactory.error();
                break;
            default:
                ModuleFactory.error();
                break;
        }
        // Should be unreachable
        return null;
    }

    private static void error() {
        throw new RuntimeException("Invalid module - needs to be added to Factory!");
    }
}
