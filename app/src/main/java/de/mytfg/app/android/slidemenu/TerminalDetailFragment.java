package de.mytfg.app.android.slidemenu;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.terminal.TerminalTopic;
import de.mytfg.app.android.modules.terminal.objects.Topic;
import de.mytfg.app.android.slidemenu.items.Navigation;
import de.mytfg.app.android.utils.TimeUtils;

/**
 * Fragment for the Terminal Topic Detail View.
 */
public class TerminalDetailFragment extends AbstractFragment {
    View detailview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailview = inflater.inflate(R.layout.terminal_detail_layout, container, false);

        TerminalTopic module = (TerminalTopic) MyTFG.moduleManager.getModule(Modules.TERMINALTOPIC);
        module.setId(args.getLong("topic", 0));

        module.getTopic(new TerminalTopic.GetTopicCallback() {
            @Override
            public void callback(Topic topic, boolean stillLoading) {
                display(topic);
            }
        });

        return detailview;
    }

    private void display(Topic topic) {
        TableRow row;

        row = addRow();
        addCell(row, true, "Allgemeines", 2);


        row = addRow();
        addCell(row, true, "Titel");
        addCell(row, false, topic.getTitle());

        row = addRow();
        addCell(row, true, "Erstellt von");
        addCell(row, false, topic.getAuthor().toString());

        row = addRow();
        String until = topic.getDeadline() >= 0 ? TimeUtils.getDateString(topic.getDeadline()) : "Keine Deadline";
        addCell(row, true, "Deadline");
        addCell(row, false, until);

        row = addRow();
        addCell(row, true, "Erstellt am");
        addCell(row, false, TimeUtils.getDateStringComplete(topic.getCreated()));

        row = addRow();
        addCell(row, true, "Bearbeitet am");
        addCell(row, false, TimeUtils.getDateStringComplete(topic.getEdited()));

        // WORKERS
        row = addRow();
        addCell(row, true, "Bearbeiter", 2);
        if (topic.getWorkers().size() > 0) {
            for (int i = 0; i < topic.getWorkers().size(); ++i) {
                row = addRow();
                addCell(row, false, topic.getWorkers().get(i).toString(), 2);
            }
        } else {
            row = addRow();
            addCell(row, false, "Keine Bearbeiter", 2);
        }

        // FLAGS
        row = addRow();
        addCell(row, true, "Flags", 2);
        if (topic.getFlags().size() > 0) {
            for (int i = 0; i < topic.getFlags().size(); ++i) {
                row = addRow();
                addCell(row, false, topic.getFlags().get(i).getName(), 2);
            }
        } else {
            row = addRow();
            addCell(row, false, "Keine Flags", 2);
        }

        // DEPENDENCIES
        row = addRow();
        addCell(row, true, "Abhängigkeiten", 2);
        if (topic.getDependencies().size() > 0) {
            for (int i = 0; i < topic.getDependencies().size(); ++i) {
                final Topic dependency = topic.getDependencies().get(i);
                row = addRow();

                addCell(row, false, "#" + dependency.getId() + " - " + dependency.getTitle(), 2);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle args = new Bundle();
                        args.putLong("topic", dependency.getId());
                        args.putString("title", dependency.getTitle());
                        MainActivity.navigation.navigate(Navigation.ItemNames.TERMINAL_TOPIC, args);
                    }
                });
            }
        } else {
            row = addRow();
            addCell(row, false, "Keine Abhängigkeiten", 2);
        }
    }

    private TableRow addRow() {
        TableRow row = new TableRow(detailview.getContext());
        row.setPadding(0, 0, 0, 0);
        TableLayout table = (TableLayout)(this.detailview.findViewById(R.id.terminal_details_table));
        table.addView(row);
        return row;
    }

    private void addCell(TableRow row, boolean isTitle, String text) {
        addCell(row, isTitle, text, 1);
    }

    private void addCell(TableRow row, boolean isTitle, String text, int colspan) {
        TextView textView = new TextView(detailview.getContext());
        if (isTitle) {
            textView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            textView.setBackgroundColor(MyTFG.color(R.color.blue_accent));
        }
        textView.setPadding(25, 25, 25, 25);
        textView.setText(text);

        row.addView(textView);

        TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
        params.span = colspan;
        textView.setLayoutParams(params);
    }
}
