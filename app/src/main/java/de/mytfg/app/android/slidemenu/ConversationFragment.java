package de.mytfg.app.android.slidemenu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.messagecenter.Messages;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;
import de.mytfg.app.android.modules.messagecenter.objects.Message;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.utils.TimeUtils;
import in.uncod.android.bypass.Bypass;

public class ConversationFragment extends AbstractFragment {

    private View view;
    private RecyclerView recyclerView;
    private ConversationAdapter conversationAdapter;
    private EditText messageEditText;
    private Messages messages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!MyTFG.isLoggedIn()) {
            MainActivity.navigation.navigate(Navigation.ItemNames.SETTINGS);
            return null;
        }

        view = inflater.inflate(R.layout.conversation_view_layout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.messagesList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MyTFG.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        conversationAdapter = new ConversationAdapter();
        recyclerView.setAdapter(conversationAdapter);

        messageEditText = (EditText) view.findViewById(R.id.messageSendText);
        view.findViewById(R.id.messageReplyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = messageEditText.getText().toString();
                Log.d("onclick", text);
                messages.sendMessage(new Messages.SendMessageConfirmedCallback() {
                    @Override
                    public void callback(boolean success) {
                        if (!success) {
                            Toast.makeText(MyTFG.getAppContext(), R.string.error_sending, Toast.LENGTH_LONG).show();
                        } else {
                            messageEditText.setText("");
                            messages.refresh();
                        }
                    }
                }, text);
            }
        });

        messages = (Messages) MyTFG.moduleManager.getModule(Modules.CONVERSATION);
        messages.setConversationId(args.getLong("conversationId"));
        messages.setOnConversationReceived(new Messages.OnConversationReceived() {
            @Override
            public void callback(Conversation conversation, boolean success) {
                if(!success) {
                    Toast.makeText(MyTFG.getAppContext(), R.string.error_refreshing, Toast.LENGTH_LONG).show();
                } else {
                    updateConversation(conversation);
                }
            }
        });
        messages.refresh();

        //TODO: hide keyboard on leave

        return view;
    }

    private void updateConversation(Conversation conversation) {
        conversationAdapter.setConversations(conversation);
        if(MainActivity.navigation.getCurrentItem().getItem() == this.item.getItem()) {
            ((MainActivity)MainActivity.context).getSupportActionBar().setTitle(messages.getLastPulledConversation().getSubject());
        }
        //TODO: mark read
    }

    @Override
    public void onResume() {
        super.onResume();
        MyTFG.gcmManager.hide("conversation-" + messages.getConversationId());
    }

}

class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Conversation conversation = new Conversation();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_message_view_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.ViewHolder holder, int position) {
        Message message = getMessage(position);
        TextView author = (TextView) holder.view.findViewById(R.id.messageAuthor);
        if (message.getAuthor().getId() == MyTFG.getUserId()) {
            author.setText(MyTFG.getAppContext().getString(R.string.me));
        } else {
            author.setText(message.getAuthor().getFirstname() + " " + message.getAuthor().getLastname());
        }
        Bypass bypass = new Bypass(MyTFG.getAppContext());
        CharSequence text = bypass.markdownToSpannable(message.getText().replace("\\n", "\n").replace("\n", "\n\n"));
        ((TextView)holder.view.findViewById(R.id.messageText)).setText(text);
        long timestamp = (long) message.getTimestamp();
        ((TextView)holder.view.findViewById(R.id.messageDate)).setText(TimeUtils.getDateStringShort(timestamp));
    }

    private Message getMessage(int i) {
        i = (getItemCount() - 1) - i;
        return conversation.getMessages().get(i);
    }

    @Override
    public int getItemCount() {
        if(conversation == null || conversation.getMessages() == null) {
            return 0;
        }
        return conversation.getMessages().size();
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversations(Conversation conversation) {
        this.conversation = conversation;
        notifyDataSetChanged();
    }
}
