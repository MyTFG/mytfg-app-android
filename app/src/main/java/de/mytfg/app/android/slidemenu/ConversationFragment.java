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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.messagecenter.Messages;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;
import de.mytfg.app.android.modules.messagecenter.objects.Message;
import de.mytfg.app.android.utils.TimeUtils;

public class ConversationFragment extends AbstractFragment {

    private static final int MESSAGES_COUNT = 500;

    private View view;
    private RecyclerView recyclerView;
    private ConversationAdapter conversationAdapter;
    private EditText messageEditText;
    private Messages messages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.conversation_view_layout, container, false);

        //TODO: not logged in

        recyclerView = (RecyclerView) view.findViewById(R.id.messagesList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MyTFG.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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
                        }
                    }
                }, text);
            }
        });

        messages = (Messages) MyTFG.moduleManager.getModule(Modules.CONVERSATION);
        messages.setConversationId(args.getLong("conversationId"));
        messages.getConversation(new Messages.GetConversationCallback() {
            @Override
            public void callback(Conversation conversation, boolean success) {
                if(!success) {
                    Toast.makeText(MyTFG.getAppContext(), R.string.error_refreshing, Toast.LENGTH_LONG).show();
                }
                conversationAdapter.setConversations(conversation); // TODO: thread safe? Also conversationslist
                recyclerView.scrollToPosition(conversationAdapter.getItemCount() - 1);
                //TODO: mark read
            }
        }, MESSAGES_COUNT);

        return view;
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
        Message message = conversation.getMessages().get(position);
        TextView author = (TextView) holder.view.findViewById(R.id.messageAuthor);
        if (message.getAuthor().getId() == MyTFG.getUserId()) {
            author.setText(MyTFG.getAppContext().getString(R.string.me));
        } else {
            author.setText(message.getAuthor().getFirstname() + " " + message.getAuthor().getLastname());
        }
        ((TextView)holder.view.findViewById(R.id.messageText)).setText(message.getText());
        long timestamp = (long) message.getTimestamp();
        ((TextView)holder.view.findViewById(R.id.messageDate)).setText(TimeUtils.getDateStringShort(timestamp));
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
