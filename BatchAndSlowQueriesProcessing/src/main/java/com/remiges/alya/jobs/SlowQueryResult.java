package com.remiges.alya.jobs;

import java.util.List;
import java.util.Map;

public class SlowQueryResult {
	private BatchStatus status;
	private String result;
	private List<AlyaErrorMessage> messages;
	private Map<String, String> outputFiles;
	private Exception error;

	public SlowQueryResult(BatchStatus status, String result, List<AlyaErrorMessage> messages,
			Map<String, String> outputFiles, Exception error) {
		this.status = status;
		this.result = result;
		this.messages = messages;
		this.outputFiles = outputFiles;
		this.error = error;
	}

	public BatchStatus getStatus() {
		return status;
	}

	public String getResult() {
		return result;
	}

	public List<AlyaErrorMessage> getMessages() {
		return messages;
	}

	public Map<String, String> getOutputFiles() {
		return outputFiles;
	}

	public Exception getError() {
		return error;
	}

	// Nested AlyaErrorMessage class
	public static class AlyaErrorMessage {
		private String message;

		public AlyaErrorMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
