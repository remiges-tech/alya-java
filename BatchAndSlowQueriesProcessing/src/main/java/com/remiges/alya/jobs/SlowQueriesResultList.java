package com.remiges.alya.jobs;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class SlowQueriesResultList {
	private String id;
	private String app;
	private String op;
	private String inputfile;
	private BatchStatus status;
	private Date reqat;
	private Date doneat;
	private Map<String, String> outputfiles;

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

	public Date getReqat() {
		return reqat;
	}

	public void setReqat(Date timestamp) {
		this.reqat = timestamp;
	}

	public Date getDoneat() {
		return doneat;
	}

	public void setDoneat(Date doneat) {
		this.doneat = doneat;
	}

	public Map<String, String> getOutputfiles() {
		return outputfiles;
	}

	public void setOutputfiles(Map<String, String> outputfiles) {
		this.outputfiles = outputfiles;
	}

}
