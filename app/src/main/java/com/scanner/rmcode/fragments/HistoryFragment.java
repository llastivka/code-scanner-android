package com.scanner.rmcode.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.scanner.rmcode.MainActivity;
import com.scanner.rmcode.R;
import com.scanner.rmcode.database.DatabaseHelper;
import com.scanner.rmcode.model.HistoryRecord;

import java.util.List;
import java.util.logging.Logger;

public class HistoryFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(HistoryFragment.class.getName());

    private Context mContext;
    private Activity mFragmentActivity;

    private DatabaseHelper historyDB;
    private TableLayout historyTable;
    private List<HistoryRecord> historyList;

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

        historyTable = view.findViewById(R.id.history_table);

        historyDB = new DatabaseHelper(mContext);
        historyList = historyDB.getAllHistoryRecords();
        if (historyList == null || historyList.isEmpty()) {
            TextView noHistoryMessage = view.findViewById(R.id.no_history_text);
            noHistoryMessage.setVisibility(View.VISIBLE);
            historyTable.setVisibility(View.GONE);
            logger.info("There is no history yet.");
        } else {
            buildHistoryTable(historyList);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buildHistoryTable(List<HistoryRecord> historyList) {
        logger.info("Building of history table has started. Number of records: " + historyList.size());
        final float scale = getContext().getResources().getDisplayMetrics().density;
        TableRow.LayoutParams messageParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        TableRow.LayoutParams dateParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        dateParams.setMargins(0,0,(int) (10 * scale + 0.5f), 0);

        TableRow head = new TableRow(mFragmentActivity);
        head.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView dateHeader = new TextView(mFragmentActivity);
        dateHeader.setLayoutParams(dateParams);
        dateHeader.getLayoutParams().width = (int) (90 * scale + 0.5f);
        dateHeader.setText(getString(R.string.date_header));
        dateHeader.setTypeface(null, Typeface.BOLD);
        head.addView(dateHeader);
        TextView messageHeader = new TextView(mFragmentActivity);
        messageHeader.setLayoutParams(messageParams);
        messageHeader.setText(getString(R.string.message_header));
        messageHeader.setTypeface(null, Typeface.BOLD);
        head.addView(messageHeader);
        historyTable.addView(head);
        addRowSeparator(historyTable);

        for (final HistoryRecord record : historyList) {
            TableRow row = new TableRow(mFragmentActivity);
            row.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView date = new TextView(mFragmentActivity);
            date.setLayoutParams(dateParams);
            date.getLayoutParams().width = (int) (80 * scale + 0.5f);
            date.setText(record.getDate());
            row.addView(date);
            TextView message = new TextView(mFragmentActivity);
            message.setLayoutParams(messageParams);
            message.setText(record.getMessage());
            row.addView(message);
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

    private void addRowSeparator(TableLayout table) {
        View v = new View(mFragmentActivity);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        v.setBackgroundColor(Color.rgb(51, 51, 51));
        table.addView(v);
    }
}
