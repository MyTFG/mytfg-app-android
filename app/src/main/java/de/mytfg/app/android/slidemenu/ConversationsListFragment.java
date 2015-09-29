package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.messagecenter.Conversations;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.utils.TimeUtils;

public class ConversationsListFragment extends AbstractFragment {

    private View view;
    private RecyclerView recyclerView;
    private ConversationsAdapter adapter;
    private Conversations conversations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.conversation_list_layout, container, false);

        conversations = (Conversations) MyTFG.moduleManager.getModule(Modules.CONVERSATIONS);

        recyclerView = (RecyclerView) view.findViewById(R.id.viewConversations);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MyTFG.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationsAdapter();
        recyclerView.setAdapter(adapter);

        conversations.setOnConversationsReceived(new Conversations.OnConversationsReceived() {
            @Override
            public void callback(List<Conversation> list, boolean success) {
                if (!success) {
                    Toast.makeText(MyTFG.getAppContext(), R.string.error_refreshing, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    adapter.setConversations(list);
                }
            }
        });

        conversations.refresh();

        view.findViewById(R.id.conversation_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: create conversation
                Toast.makeText(MyTFG.getAppContext(), "Not implemented yet!", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}

class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private List<Conversation> conversations = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_cardview_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        final Conversation conversation = conversations.get(position);
        ((TextView)holder.view.findViewById(R.id.conversationTitle)).setText(conversation.getSubject());
        long timestamp = (long) conversation.getLastMessageTimestamp();
        String text = MyTFG.getAppContext().getResources().getText(R.string.last_message) + ": " + conversation.getLastMessageAuthor().getFirstname() + " " + conversation.getLastMessageAuthor().getLastname() + " " + TimeUtils.getDateStringComplete(timestamp);
        ((TextView)holder.view.findViewById(R.id.conversationText)).setText(text);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong("conversationId", conversation.getId());
                MainActivity.navigation.navigate(Navigation.ItemNames.CONVERSATION, args, Navigation.Transition.SLIDE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }
}
