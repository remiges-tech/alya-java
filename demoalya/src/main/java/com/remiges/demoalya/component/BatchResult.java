package com.remiges.demoalya.component;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class BatchResult {
	private StatusEnum status;
	private JsonNode result;
	private List<ErrorMessage> messages;
	private Map<String, String> outputFiles;

	public BatchResult(StatusEnum status, JsonNode result, List<ErrorMessage> messages,
			Map<String, String> outputFiles) {
		this.status = status;
		this.result = result;
		this.messages = messages;
		this.outputFiles = outputFiles;
	}

	public enum StatusEnum {
		SUCCESS, FAILED, TRY_LATER
	}

	public class ErrorMessage {
		private String message;

		public ErrorMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public JsonNode getResult() {
		return result;
	}

	public void setResult(JsonNode result) {
		this.result = result;
	}

	public List<ErrorMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ErrorMessage> messages) {
		this.messages = messages;
	}

	public Map<String, String> getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(Map<String, String> outputFiles) {
		this.outputFiles = outputFiles;
	}

}
