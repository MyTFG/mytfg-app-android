package de.mytfg.app.android.modulemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a Module Manager
 */
public class ModuleManager {
    private Map<Modules, Module> modules;
    public ModuleManager() {
        modules = new HashMap<>();
    }

    public Module getModule(Modules module) {
        if (modules.containsKey(module)) {
            // Instance exists
            return modules.get(module);
        } else {
            // Create new instance
            Module mod = ModuleFactory.createModule(module);
            if (mod != null) {
                modules.put(module, mod);
                return mod;
            }
        }
        throw new RuntimeException("Null module");
    }
}
