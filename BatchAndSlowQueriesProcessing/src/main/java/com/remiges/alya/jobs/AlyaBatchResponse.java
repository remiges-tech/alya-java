package com.remiges.alya.jobs;

public class AlyaBatchResponse {

    private String batchID;
    private int totalRows;

    public AlyaBatchResponse(String batchID, int totalRows) {
        this.batchID = batchID;
        this.totalRows = totalRows;
    }

    // Getters and setters
    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}

