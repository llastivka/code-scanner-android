package com.scanner.rmcode.model;

public class HistoryRecord {

    private int recordId;
    private String Date;
    private String message;
    private String notes;

    public HistoryRecord() {
    }

    public HistoryRecord(int recordId, String date, String message, String notes) {
        this.recordId = recordId;
        Date = date;
        this.message = message;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
