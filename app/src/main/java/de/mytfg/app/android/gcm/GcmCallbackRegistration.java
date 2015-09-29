package de.mytfg.app.android.gcm;

import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.api.MytfgApi;
import de.mytfg.app.android.modulemanager.ModuleManager;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.messagecenter.Conversations;
import de.mytfg.app.android.modules.messagecenter.Messages;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;
import de.mytfg.app.android.slidemenu.MainActivity;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.slidemenu.items.NavigationItem;

/**
 * Registers all GCM Events. Add your event here.
 */
public class GcmCallbackRegistration {
    public static void registerAll() {
        registerTerminal();
    }

    private static void registerTerminal() {
        // Set callback for Terminal Pushs
        MyTFG.gcmManager.setClickCallback("terminal", new GcmCallback() {
            @Override
            public void callback(GcmNotification notification) {
                String[] grouper = notification.getGrouper().split("-");
                if (grouper.length == 3) {
                    int id = Integer.parseInt(grouper[2]);
                    Bundle args = new Bundle();
                    args.putLong("topic", id);
                    args.putString("title", "Laden...");
                    MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL_TOPIC, args);
                }
            }
        });
        // Set callback for Messages
        MyTFG.gcmManager.setReceiveCallback("message", new GcmCallback() {
            @Override
            public void callback(GcmNotification notification) {
                String[] grouper = notification.getGrouper().split("-");
                if(grouper.length == 2 && grouper[0].equals("conversation")) {
                    Messages messages = (Messages) MyTFG.moduleManager.getModule(Modules.CONVERSATION);
                    long conversationId = Long.parseLong(grouper[1]);
                    if(messages.getConversationId() == conversationId) {
                        messages.refresh();
                        if(MainActivity.isVisible() && MainActivity.navigation.getCurrentItem().getItem() == Navigation.ItemNames.CONVERSATION) {
                            MyTFG.gcmManager.hide(notification);
                        }
                    }
                    Conversations conversations = (Conversations) MyTFG.moduleManager.getModule(Modules.CONVERSATIONS);
                    if(conversations.getLastPulledConversations() != null) {
                        boolean newConversation = true;
                        for (Conversation conversation : conversations.getLastPulledConversations()) {
                            if(conversation.getId() == conversationId) {
                                newConversation = false;
                                break;
                            }
                        }
                        if(newConversation) {
                            conversations.refresh();
                            if(MainActivity.isVisible() && MainActivity.navigation.getCurrentItem().getItem() == Navigation.ItemNames.CONVERSATIONS_LIST) {
                                MyTFG.gcmManager.hide(notification);
                            }
                        }
                    }
                }
            }
        });
        MyTFG.gcmManager.setClickCallback("message", new GcmCallback() {
            @Override
            public void callback(GcmNotification notification) {
                String[] grouper = notification.getGrouper().split("-");
                if (grouper.length == 2 && grouper[0].equals("conversation")) {
                    long conversationId = Long.parseLong(grouper[1]);
                    Bundle args = new Bundle();
                    args.putLong("conversationId", conversationId);
                    MainActivity.navigation.navigate(Navigation.ItemNames.CONVERSATION, args);
                } else {
                    Toast toast = Toast.makeText(MyTFG.getAppContext(), "Unknown Grouper", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
}
