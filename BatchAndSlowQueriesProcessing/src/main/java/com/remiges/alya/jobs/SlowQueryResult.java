package com.remiges.alya.jobs;

import java.util.List;
import java.util.Map;

public class SlowQueryResult {
	private BatchStatus status;
	private Map<String, String> result;
	private Map<String, String> messages;
	private Map<String, String> outputFiles;
	private Exception error;

	public SlowQueryResult(BatchStatus status, Map<String, String> result, Map<String, String> messages,
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

	public Map<String, String> getResult() {
		return result;
	}

	public Map<String, String> getMessages() {
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
