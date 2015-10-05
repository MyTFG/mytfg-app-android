package de.mytfg.app.android.slidemenu;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.mytfg.app.android.MyTFG;
import de.mytfg.app.android.R;
import de.mytfg.app.android.modulemanager.Modules;
import de.mytfg.app.android.modules.general.User;
import de.mytfg.app.android.modules.terminal.TerminalCreator;
import de.mytfg.app.android.modules.terminal.objects.Flag;
import de.mytfg.app.android.modules.terminal.objects.Topic;
import de.mytfg.app.android.utils.TimeUtils;

/**
 * Creates new Terminal Topic.
 */
public class TerminalCreateFragment extends AbstractFragment {
    View createView;
    TabHost tabHost;
    final TerminalCreator module;

    public TerminalCreateFragment() {
        super();
        module = (TerminalCreator) MyTFG.moduleManager.getModule(Modules.TERMINALCREATOR);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createView = inflater.inflate(R.layout.terminal_create_layout, container, false);

        tabHost = (TabHost)createView.findViewById(R.id.tabHost);
        setup();

        return createView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.terminal_topic_create_menu, menu);
    }

    private void setup() {
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("TAB1");
        tab1.setIndicator("Allgemeines");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        setupTab1();

        TabHost.TabSpec tab2 = tabHost.newTabSpec("TAB2");
        tab2.setIndicator("Zusätzliches");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);
        setupTab2();

        TabHost.TabSpec tab3 = tabHost.newTabSpec("TAB3");
        tab3.setIndicator("Bearbeiter");
        tab3.setContent(R.id.tab3);
        tabHost.addTab(tab3);
        setupTab3();

        TabHost.TabSpec tab4 = tabHost.newTabSpec("TAB4");
        tab4.setIndicator("Abhängigkeiten");
        tab4.setContent(R.id.tab4);
        tabHost.addTab(tab4);
        setupTab4();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                InputMethodManager imm = (InputMethodManager) createView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(createView.getWindowToken(), 0);
            }
        });

        module.setOnResetListener(new TerminalCreator.OnResetListener() {
            @Override
            public void onReset() {
                update();
            }
        });

        update();
    }

    private void update() {
        EditText title = (EditText)createView.findViewById(R.id.edit_title);
        EditText text = (EditText)createView.findViewById(R.id.edit_text);
        Switch deadlineToggle = (Switch)createView.findViewById(R.id.deadlineSwitch);
        TextView deadline = (TextView)createView.findViewById(R.id.deadlineText);
        ListView flags = (ListView)createView.findViewById(R.id.flagList);
        ListView workers = (ListView)createView.findViewById(R.id.workerList);
        ListView dependencies = (ListView)createView.findViewById(R.id.dependencyList);

        title.setText(module.getTitle());
        text.setText(module.getText());
        deadlineToggle.setChecked(module.hasDeadline());
        deadline.setText(TimeUtils.getDateString(module.getDeadline() / 1000, true));
        if (flags.getAdapter() != null) {
            ((TerminalListAdapter) flags.getAdapter()).notifyDataSetChanged();
        }
        if (workers.getAdapter() != null) {
            ((TerminalListAdapter) workers.getAdapter()).notifyDataSetChanged();
        }
        if (dependencies.getAdapter() != null) {
            ((TerminalListAdapter) dependencies.getAdapter()).notifyDataSetChanged();
        }
    }

    private void setupTab1() {
        final EditText title = (EditText)createView.findViewById(R.id.edit_title);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                module.setTitle(title.getText().toString());
            }
        });

        final EditText text = (EditText)createView.findViewById(R.id.edit_text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                module.setText(text.getText().toString());
            }
        });
    }

    private void setupTab2() {
        final TextView deadlineText = (TextView)createView.findViewById(R.id.deadlineText);
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        final Switch deadlineSwitch = (Switch)createView.findViewById(R.id.deadlineSwitch);
        Calendar newCalendar = Calendar.getInstance();


        final DatePickerDialog deadlinePicker = new DatePickerDialog(createView.getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker,  int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                module.setDeadline(newDate.getTimeInMillis());
                deadlineText.setText(dateFormatter.format(newDate.getTime()));
                deadlineSwitch.setChecked(true);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        deadlineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deadlinePicker.show();
            }
        });

        deadlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                module.setHasDeadline(b);
                if (b) {
                    deadlineText.setTextColor(MyTFG.color(R.color.white));
                } else {
                    deadlineText.setTextColor(MyTFG.color(R.color.gray));
                }
            }
        });

        final ListView flagList = (ListView)createView.findViewById(R.id.flagList);

        module.getFlagList(new TerminalCreator.FlagListCallback() {
            @Override
            public void callback(List<Flag> list) {
                TerminalListAdapter<Flag> adapter = new TerminalListAdapter<>(createView.getContext(),
                        R.layout.terminal_create_list_item, list);
                flagList.setAdapter(adapter);
            }
        });

        flagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Flag flag = (Flag) flagList.getItemAtPosition(i);
                if (module.hasFlag(flag)) {
                    module.removeFlag(flag);
                } else {
                    module.addFlag(flag);
                }
                ((TerminalListAdapter) flagList.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    private void setupTab3() {
        final ListView workerList = (ListView)createView.findViewById(R.id.workerList);

        module.getWorkerList(new TerminalCreator.WorkerListCallback() {
            @Override
            public void callback(List<User> list) {
                TerminalListAdapter<User> adapter = new TerminalListAdapter<>(createView.getContext(),
                        R.layout.terminal_create_list_item, list);
                workerList.setAdapter(adapter);
            }
        });

        workerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) workerList.getItemAtPosition(i);
                if (module.hasWorker(user)) {
                    module.removeWorker(user);
                } else {
                    module.addWorker(user);
                }
                ((TerminalListAdapter) workerList.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    private void setupTab4() {
        final ListView dependencyList = (ListView)createView.findViewById(R.id.dependencyList);

        module.getDependencyList(new TerminalCreator.DependencyListCallback() {
            @Override
            public void callback(List<Topic> list) {
                TerminalListAdapter<Topic> adapter = new TerminalListAdapter<>(createView.getContext(),
                        R.layout.terminal_create_list_item, list);
                dependencyList.setAdapter(adapter);
            }
        });

        dependencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Topic topic = (Topic) dependencyList.getItemAtPosition(i);
                if (module.hasDependency(topic)) {
                    module.removeDependency(topic);
                } else {
                    module.addDependency(topic);
                }
                ((TerminalListAdapter) dependencyList.getAdapter()).notifyDataSetChanged();
            }
        });
    }


    public class TerminalListAdapter<E> extends ArrayAdapter<E> {
        public TerminalListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public TerminalListAdapter(Context context, int textViewResourceId, List<E> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.terminal_create_list_item, null);
            }

            E item = getItem(position);

            if (item != null) {
                TextView text = (TextView)v.findViewById(R.id.listItemText);
                if (text != null) {
                    text.setText(item.toString());
                }
                TerminalCreator module = (TerminalCreator)MyTFG.moduleManager.getModule(Modules.TERMINALCREATOR);
                if (item instanceof Flag) {
                    Flag flag = (Flag)item;
                    if (module.hasFlag(flag)) {
                        v.setBackgroundColor(MyTFG.color(R.color.orange_accent));
                    } else {
                        v.setBackgroundColor(MyTFG.color(R.color.blue_accent));
                    }
                } else if (item instanceof User) {
                    User user = (User)item;
                    if (module.hasWorker(user)) {
                        v.setBackgroundColor(MyTFG.color(R.color.orange_accent));
                    } else {
                        v.setBackgroundColor(MyTFG.color(R.color.blue_accent));
                    }
                }else if (item instanceof Topic) {
                    Topic topic = (Topic)item;
                    if (module.hasDependency(topic)) {
                        v.setBackgroundColor(MyTFG.color(R.color.orange_accent));
                    } else {
                        v.setBackgroundColor(MyTFG.color(R.color.blue_accent));
                    }
                }

            }

            return v;
        }
    }

}
