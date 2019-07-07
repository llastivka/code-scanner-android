package com.scanner.rmcode.model;

public class HistoryRecord {

    private int recordId;
    private String Date;
    private String message;

    public HistoryRecord() {
    }

    public HistoryRecord(int recordId, String date, String message) {
        this.recordId = recordId;
        Date = date;
        this.message = message;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
