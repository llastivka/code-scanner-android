package com.scanner.rmcode.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.scanner.rmcode.model.HistoryRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final Logger logger = Logger.getLogger(DatabaseHelper.class.getName());

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "history.db";
    private static final String TABLE_NAME = "records";
    private static final String ID_COLUMN = "ID";
    private static final String DATE_COLUMN = "DATE";
    private static final String RESULT_COLUMN = "RESULT";
    private static final String NOTES_COLUMN = "NOTES";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, RESULT TEXT, NOTES TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertRecord(String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put(DATE_COLUMN, dateFormat.format(new Date()));
        contentValues.put(RESULT_COLUMN, result);
        long res = db.insert(TABLE_NAME, null, contentValues);
        return res != -1;
    }

    public List<HistoryRecord> getAllHistoryRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " +DATE_COLUMN + " DESC" , null);
        List<HistoryRecord> historyList = new ArrayList<>();
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()) {
                historyList.add(new HistoryRecord(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3)));
            }
        } else {
            logger.info("No records returned from database");
        }
        return historyList;
    }

    public boolean addNotes(Integer id, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_COLUMN, notes);
        long res = db.update(TABLE_NAME, contentValues, ID_COLUMN + "=" + id, null);
        return res != -1;
    }

    public boolean deleteSpecificRecords(List<Integer> ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean allSuccesfull = true;
        for (Integer id : ids) {
            long res = db.delete(TABLE_NAME, ID_COLUMN + "=" + id, null);
            if (res == -1) {
                allSuccesfull = false;
            }
        }
        return allSuccesfull;
    }

    public boolean deleteSpecificRecord(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(TABLE_NAME, ID_COLUMN + "=" + id, null);
        return res != -1;
    }

    public boolean deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(TABLE_NAME, null, null);
        return res != -1;
    }
}
