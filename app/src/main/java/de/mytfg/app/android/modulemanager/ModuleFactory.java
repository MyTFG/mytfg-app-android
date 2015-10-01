package de.mytfg.app.android.modulemanager;

import de.mytfg.app.android.modules.messagecenter.Conversations;
import de.mytfg.app.android.modules.messagecenter.Messages;
import de.mytfg.app.android.modules.settings.Settings;
import de.mytfg.app.android.modules.terminal.TerminalTopics;
import de.mytfg.app.android.modules.terminal.TerminalTopic;

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
            case CONVERSATIONS:
                return new Conversations();
            case CONVERSATION:
                return new Messages();
            case TERMINALTOPICS:
                return new TerminalTopics();
            case TERMINALTOPIC:
                return new TerminalTopic();
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
