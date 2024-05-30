package com.remiges.alya.jobs;

import java.util.Map;

public class BatchOutputResult {

	private BatchStatus status;
	private Map<String, String> batchOutput;
	private Map<String, String> outputFiles;
	private int nsuccess;
	private int nfailed;
	private int naborted;
	private String err;

	public BatchOutputResult() {
	}

	public BatchOutputResult(BatchStatus status, Map<String, String> batchOutput, Map<String, String> outputFiles,
			int nsuccess, int nfailed, int naborted, String err) {
		this.status = status;
		this.batchOutput = batchOutput;
		this.outputFiles = outputFiles;
		this.nsuccess = nsuccess;
		this.nfailed = nfailed;
		this.naborted = naborted;
		this.err = err;
	}

	public BatchStatus getStatus() {
		return status;
	}

	public void setStatus(BatchStatus status) {
		this.status = status;
	}

	public Map<String, String> getBatchOutput() {
		return batchOutput;
	}

	public void setBatchOutput(Map<String, String> batchOutput) {
		this.batchOutput = batchOutput;
	}

	public Map<String, String> getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(Map<String, String> outputFiles) {
		this.outputFiles = outputFiles;
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

	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

}
