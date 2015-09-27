package de.mytfg.app.android.slidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.ModuleFactory;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.messagecenter.Conversations;
import de.mytfg.app.android.modules.messagecenter.objects.Conversation;
import de.mytfg.app.android.utils.TimeUtils;

public class ConversationsListFragment extends AbstractFragment {

    private View view;
    private RecyclerView recyclerView;
    private ConversationsAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private Conversations conversations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.conversation_list_layout, container, false);

        conversations = (Conversations) ModuleFactory.createModule(Modules.CONVERSATIONS);

        recyclerView = (RecyclerView) view.findViewById(R.id.viewConversations);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MyTFG.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationsAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.viewSwipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();

        return view;
    }

    public void refresh() {
        conversations.getConversations(new Conversations.GetConversationsCallback() {
            @Override
            public void callback(List<Conversation> list, boolean waiting, boolean success) {
                swipeRefresh.setRefreshing(conversations.isUpdating());
                if(!success) {
                    Toast.makeText(MyTFG.getAppContext(), R.string.error_refreshing, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    adapter.setConversations(list);
                }
            }
        }, true);
    }
}

class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    private List<Conversation> conversations = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_cardview_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Conversation conversation = conversations.get(position);
        ((TextView)holder.view.findViewById(R.id.conversationTitle)).setText(conversation.getSubject());
        long timestamp = (long) conversation.getLastMessageTimestamp();
        Log.d("", timestamp + " " + conversation.getLastMessageTimestamp());
        String text = "Letzte Nachricht: " + conversation.getLastMessageAuthor().getFirstname() + " " + conversation.getLastMessageAuthor().getLastname() + " " + TimeUtils.getDateStringComplete(timestamp);
        ((TextView)holder.view.findViewById(R.id.conversationText)).setText(text);
    }

    // Return the size of your dataset (invoked by the layout manager)
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
