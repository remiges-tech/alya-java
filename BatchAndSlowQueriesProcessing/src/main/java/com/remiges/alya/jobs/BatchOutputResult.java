package com.remiges.alya.jobs;

import java.util.List;
import java.util.Map;

import com.remiges.alya.jobs.SlowQueryResult.AlyaErrorMessage;

public class BatchOutputResult {

	private BatchStatus status;
	private List<BatchOutput_t> batchOutput;
	private Map<String, String> outputFiles;
	private int nsuccess;
	private int nfailed;
	private int naborted;
	private ErrorCodes err;

	public BatchOutputResult() {
	}

	public BatchOutputResult(BatchStatus status, List<BatchOutput_t> batchOutput_t, Map<String, String> outputFiles,
			int nsuccess, int nfailed, int naborted, ErrorCodes err) {
		this.status = status;
		this.batchOutput = batchOutput_t;
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

	public List<BatchOutput_t> getBatchOutput() {
		return batchOutput;
	}

	public void setBatchOutput(List<BatchOutput_t> batchOutput) {
		this.batchOutput = (List<BatchOutput_t>) batchOutput;
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

	public ErrorCodes getErr() {
		return err;
	}

	public void setErr(ErrorCodes err) {
		this.err = err;
	}

	// Nested AlyaErrorMessage class
	public static class AlyaBatchErrorMessage {
		private String message;

		public AlyaBatchErrorMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

}

class BatchOutput_t {
	private int line;
	private BatchStatus status;
	private Map<String, String> res;
	private Map<String, String> messages;

	public BatchOutput_t(int line, BatchStatus status, Map<String, String> res, Map<String, String> messages) {
		super();
		this.line = line;
		this.status = status;
		this.res = res;
		this.messages = messages;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public BatchStatus getStatus() {
		return status;
	}

	public void setStatus(BatchStatus status) {
		this.status = status;
	}

	public Map<String, String> getRes() {
		return res;
	}

	public void setRes(Map<String, String> res) {
		this.res = res;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

}
