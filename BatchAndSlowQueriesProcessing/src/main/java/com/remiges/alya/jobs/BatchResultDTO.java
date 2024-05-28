package com.remiges.alya.jobs;

import java.time.LocalDateTime;
import java.util.Map;

public class BatchResultDTO {
    private String id;
    private String app;
    private String op;
    private String inputfile;
    private BatchStatus status;
    private LocalDateTime reqat;
    private LocalDateTime doneat;
    private Map<String, String> outputfiles;
    private int nrows;
    private int nsuccess;
    private int nfailed;
    private int naborted;

    // Getters and setters for each field

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getInputfile() {
        return inputfile;
    }

    public void setInputfile(String inputfile) {
        this.inputfile = inputfile;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(BatchStatus status) {
        this.status = status;
    }

    public LocalDateTime getReqat() {
        return reqat;
    }

    public void setReqat(LocalDateTime reqat) {
        this.reqat = reqat;
    }

    public LocalDateTime getDoneat() {
        return doneat;
    }

    public void setDoneat(LocalDateTime doneat) {
        this.doneat = doneat;
    }

    public Map<String, String> getOutputfiles() {
        return outputfiles;
    }

    public void setOutputfiles(Map<String, String> outputfiles) {
        this.outputfiles = outputfiles;
    }

    public int getNrows() {
        return nrows;
    }

    public void setNrows(int nrows) {
        this.nrows = nrows;
    }

    public int getNsuccess() {
        return nsuccess;
    }

    public void setNsuccess(int nsuccess) {
        this.nsuccess = nsuccess;
    }

    public int getNfailed() {
        return nfailed;
    }

    public void setNfailed(int nfailed) {
        this.nfailed = nfailed;
    }

    public int getNaborted() {
        return naborted;
    }

    public void setNaborted(int naborted) {
        this.naborted = naborted;
    }
}
