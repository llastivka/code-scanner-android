package com.scanner.rmcode.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.scanner.rmcode.MainActivity;
import com.scanner.rmcode.R;
import com.scanner.rmcode.database.DatabaseHelper;
import com.scanner.rmcode.model.HistoryRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistoryFragment extends Fragment implements BaseFragment {

    private static final Logger logger = Logger.getLogger(HistoryFragment.class.getName());

    private Context mContext;
    private Activity mFragmentActivity;

    private TableLayout historyTable;

    private float scale;
    private TableRow.LayoutParams messageParams;
    private TableRow.LayoutParams dateParams;
    private TableRow.LayoutParams editParams;
    private TableLayout.LayoutParams rowParams;

    private DatabaseHelper historyDb;

    private boolean deleteMode = false;
    private boolean deleteAll = false;
    private List<ImageButton> recordsEditBtns = new ArrayList<>();
    private List<CheckBox> recordsDeleteCheckboxes = new ArrayList<>();
    private List<Integer> recordsToDelete = new ArrayList<>();
    private TextView editHeader;
    private CheckBox mainDeleteCheckBox;
    private Button deleteCancel;

    View historyFragmentView;
    Drawable background;
    Drawable buttonDrawable;
    ColorStateList accentColor;

    private List<HistoryRecord> historyList;

    Typeface type;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        historyFragmentView = view;
        setTheme();

        type = Typeface.createFromAsset(mContext.getAssets(),"fonts/Kalam-Regular.ttf");
        setFonts(view);

        scale = mContext.getResources().getDisplayMetrics().density;
        setTableColumnsScaleParams();

        historyTable = view.findViewById(R.id.history_table);

        historyDb = new DatabaseHelper(mContext);
        historyList = historyDb.getAllHistoryRecords();
        if (historyList == null || historyList.isEmpty()) {
            TextView noHistoryMessage = view.findViewById(R.id.no_history_text);
            noHistoryMessage.setVisibility(View.VISIBLE);
            historyTable.setVisibility(View.GONE);
            logger.info("There is no history yet.");
        } else {
            buildHistoryTable(historyList);

            deleteCancel = view.findViewById(R.id.history_delete_cancel);
            deleteCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchMode(false);
                    if (mainDeleteCheckBox != null) {
                        mainDeleteCheckBox.setChecked(false);
                    }
                }
            });

            Button delete = view.findViewById(R.id.history_delete);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteMode) {
                        deleteSelectedRecords();
                    } else {
                        switchMode(true);
                    }
                }
            });
        }

        return view;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildHistoryTable(List<HistoryRecord> historyList) {
        logger.info("Building of history table has started. Number of records: " + historyList.size());

        rowParams = new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.gravity = Gravity.CENTER_VERTICAL;

        TableRow head = generateTableHeader();
        historyTable.addView(head);
        addRowSeparator(historyTable);

        for (final HistoryRecord record : historyList) {
            TableRow row = new TableRow(mFragmentActivity);
            int rowMinHeight = (int) (55 * scale + 0.5f);
            row.setLayoutParams(rowParams);
            row.setMinimumHeight(rowMinHeight);

            TextView date = new TextView(mFragmentActivity);
            date.setTypeface(type);
            date.setLayoutParams(dateParams);
            date.getLayoutParams().width = (int) (80 * scale + 0.5f);
            date.setText(record.getDate());
            row.addView(date);

            TextView message = new TextView(mFragmentActivity);
            message.setTypeface(type);
            message.setLayoutParams(messageParams);
            String notes = record.getNotes();
            String recordText = notes != null && !notes.isEmpty() ? notes : record.getMessage();
            message.setText(recordText);
            row.addView(message);

            ImageButton editButton = getEditButton(record);
            editButton.setLayoutParams(editParams);
            recordsEditBtns.add(editButton);

            CheckBox checkBox = getDeleteCheckBox(record);
            recordsDeleteCheckboxes.add(checkBox);

            row.addView(editButton);
            row.addView(checkBox);

            row.setBackground(mFragmentActivity.getDrawable(R.drawable.history_table_row));
            historyTable.addView(row);
            addRowSeparator(historyTable);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mFragmentActivity).changeToResultFragments(record.getMessage());
                }
            });
        }

        historyTable.setVisibility(View.VISIBLE);
        logger.info("Building of history table has ended");
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private TableRow generateTableHeader() {
        int rowMinHeight = (int) (55 * scale + 0.5f);
        TableRow head = new TableRow(mFragmentActivity);
        head.setLayoutParams(rowParams);

        head.setMinimumHeight(rowMinHeight);
        TextView dateHeader = new TextView(mFragmentActivity);

        dateHeader.setLayoutParams(dateParams);
        dateHeader.getLayoutParams().width = (int) (90 * scale + 0.5f);
        dateHeader.setText(getString(R.string.date_header));
        dateHeader.setTypeface(type, Typeface.BOLD);
        head.addView(dateHeader);

        TextView messageHeader = new TextView(mFragmentActivity);
        messageHeader.setLayoutParams(messageParams);
        messageHeader.setText(getString(R.string.message_header));
        messageHeader.setTypeface(type, Typeface.BOLD);
        head.addView(messageHeader);

        editHeader = new TextView(mFragmentActivity);
        editHeader.setLayoutParams(editParams);
        editHeader.getLayoutParams().width = (int) (50 * scale + 0.5f);
        editHeader.setText(getString(R.string.edit_header));
        editHeader.setTypeface(type, Typeface.BOLD);

        mainDeleteCheckBox = new CheckBox(mContext);
        mainDeleteCheckBox.setLayoutParams(editParams);
        mainDeleteCheckBox.getLayoutParams().width = (int) (50 * scale + 0.5f);
        mainDeleteCheckBox.setButtonTintList(accentColor);
        mainDeleteCheckBox.setVisibility(View.GONE);
        mainDeleteCheckBox.setHighlightColor(accentColor.getDefaultColor());
        mainDeleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkUncheckMainDeleteCheckbox(isChecked);
            }
        });

        head.addView(editHeader);
        head.addView(mainDeleteCheckBox);

        return head;
    }

    private void addRowSeparator(TableLayout table) {
        View v = new View(mFragmentActivity);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        v.setBackgroundColor(Color.rgb(51, 51, 51));
        table.addView(v);
    }

    private void checkUncheckMainDeleteCheckbox(boolean check) {
        if (check) {
            for (CheckBox cb : recordsDeleteCheckboxes) {
                cb.setChecked(true);
                deleteAll = true;
            }
            recordsToDelete = new ArrayList<>();
            for (HistoryRecord hs : historyList) {
                recordsToDelete.add(hs.getRecordId());
            }
        } else {
            if (deleteAll) {
                for (CheckBox cb : recordsDeleteCheckboxes) {
                    cb.setChecked(false);
                    deleteAll = false;
                }
                recordsToDelete = new ArrayList<>();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private ImageButton getEditButton(final HistoryRecord historyRecord) {
        ImageButton editBtn = new ImageButton(mFragmentActivity);
        editBtn.setImageResource(android.R.drawable.ic_menu_edit);
        editBtn.setBackground(mContext.getDrawable(R.drawable.basic_button));

        editBtn.setOnClickListener(new View.OnClickListener() {

            private Dialog editPopup;

            @Override
            public void onClick(View v) {
                editPopup = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar);
                editPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                editPopup.setContentView(getViewWithData());
                editPopup.setCancelable(true);
                editPopup.show();
            }

            private View getViewWithData() {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.edit_popup_fragment, null);
                setEditFonts(view);

                TextView date = view.findViewById(R.id.edit_popup_date);
                date.setText(historyRecord.getDate());

                TextView message = view.findViewById(R.id.edit_popup_message);
                message.setText(historyRecord.getMessage());

                final EditText notesView = view.findViewById(R.id.edit_popup_notes);
                final String notes = historyRecord.getNotes();
                if (notes != null && !notes.isEmpty()) {
                    notesView.setText(notes);
                }

                Button ok = view.findViewById(R.id.edit_popup_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNotes(notesView.getText().toString());
                        editPopup.dismiss();
                        reloadFragment();
                    }
                });
                ok.setBackground(buttonDrawable);

                Button cancel = view.findViewById(R.id.edit_popup_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editPopup.cancel();
                    }
                });
                cancel.setBackground(buttonDrawable);

                return view;
            }

            private void saveNotes(String notes) {
                DatabaseHelper historyDB = new DatabaseHelper(mContext);
                historyDB.addNotes(historyRecord.getRecordId(), notes);
            }
        });

        return editBtn;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    CheckBox getDeleteCheckBox(final HistoryRecord historyRecord) {
        CheckBox checkBox = new CheckBox(mContext);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recordsToDelete.add(historyRecord.getRecordId());
                } else {
                    deleteAll = false;
                    mainDeleteCheckBox.setChecked(false);
                    for (int i = 0; i < recordsToDelete.size(); i++) {
                        if (recordsToDelete.get(i) == historyRecord.getRecordId()) {
                            recordsToDelete.remove(i);
                            break;
                        }
                    }
                }
            }
        });
        checkBox.setLayoutParams(editParams);
        checkBox.setVisibility(View.GONE);
        checkBox.setButtonTintList(accentColor);
        return checkBox;
    }

    private void deleteSelectedRecords() {
        if (deleteAll) {
            historyDb.deleteAllRecords();
        } else {
            historyDb.deleteSpecificRecords(recordsToDelete);
        }
        deleteMode = false;
        reloadFragment();
    }

    private void switchMode(boolean toDeleteMode) {
        if (toDeleteMode) {
            recordsToDelete = new ArrayList<>();
            deleteAll = false;
            for (ImageButton btn : recordsEditBtns) {
                btn.setVisibility(View.GONE);
            }
            for (CheckBox cb : recordsDeleteCheckboxes) {
                cb.setVisibility(View.VISIBLE);
            }
            mainDeleteCheckBox.setVisibility(View.VISIBLE);
            editHeader.setVisibility(View.GONE);
            deleteCancel.setVisibility(View.VISIBLE);
        } else {
            for (ImageButton btn : recordsEditBtns) {
                btn.setVisibility(View.VISIBLE);
            }
            for (CheckBox cb : recordsDeleteCheckboxes) {
                cb.setVisibility(View.GONE);
            }
            mainDeleteCheckBox.setVisibility(View.GONE);
            editHeader.setVisibility(View.VISIBLE);
            deleteCancel.setVisibility(View.INVISIBLE);
        }
        deleteMode = toDeleteMode;
    }

    private void setTableColumnsScaleParams() {
        messageParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        messageParams.gravity = Gravity.CENTER_VERTICAL;

        dateParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        dateParams.setMargins(0,0,(int) (10 * scale + 0.5f), 0);
        dateParams.gravity = Gravity.CENTER_VERTICAL;

        editParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        editParams.setMargins(0,0,(int) (5 * scale + 0.5f), 0);
        editParams.gravity = Gravity.CENTER_VERTICAL;
    }

    public void reloadFragment() {
        if (getFragmentManager() != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();
        }
    }

    private void setFonts(View view) {
        TextView noHistoryMessage = view.findViewById(R.id.no_history_text);
        noHistoryMessage.setTypeface(type);

        Button deleteButton = view.findViewById(R.id.history_delete);
        deleteButton.setTypeface(type);

        Button cancelButton = view.findViewById(R.id.history_delete_cancel);
        cancelButton.setTypeface(type);
    }

    private void setEditFonts(View view) {
        TextView editDateLabel = view.findViewById(R.id.history_edit_date_label);
        editDateLabel.setTypeface(type);

        TextView editDate = view.findViewById(R.id.edit_popup_date);
        editDate.setTypeface(type);

        TextView editMessageLabel = view.findViewById(R.id.history_edit_message_label);
        editMessageLabel.setTypeface(type);

        TextView editMessage = view.findViewById(R.id.edit_popup_message);
        editMessage.setTypeface(type);

        TextView editNotesLabel = view.findViewById(R.id.history_edit_notes_label);
        editNotesLabel.setTypeface(type);

        EditText editNotes = view.findViewById(R.id.edit_popup_notes);
        editNotes.setTypeface(type);

        Button okButton = view.findViewById(R.id.edit_popup_ok);
        okButton.setTypeface(type);

        Button cancelButton = view.findViewById(R.id.edit_popup_cancel);
        cancelButton.setTypeface(type);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setThemeDetails(Drawable back, Drawable buttonBack, ColorStateList accent) {
        background = back;
        buttonDrawable = buttonBack;
        accentColor = accent;

        if (historyFragmentView != null) {
            setTheme();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTheme() {
        historyFragmentView.setBackground(background);

        Button deleteButton = historyFragmentView.findViewById(R.id.history_delete);
        deleteButton.setBackground(buttonDrawable);

        Button deleteCancelButton = historyFragmentView.findViewById(R.id.history_delete_cancel);
        deleteCancelButton.setBackground(buttonDrawable);
    }
}
