package com.remiges.alya.jobs;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.service.BatchJobService;

import jakarta.transaction.Transactional;

@Component
public class Batch {

	BatchJobService batchjobSrv;

	Batch(BatchJobService batchjobSrv) {

		this.batchjobSrv = batchjobSrv;
	}

	public String batchSubmit(String app, String op, JsonNode jsonNode, List<BatchInput> batchInput, boolean waitabit) {

		// Set the batch status based on waitabit
		BatchStatus status = waitabit ? BatchStatus.BatchWait : BatchStatus.BatchQueued;

		UUID batchId = batchjobSrv.SaveBatch(app, op, jsonNode, batchInput, status);

		return batchId.toString();
	}

	@Transactional
	public AlyaBatchResponse append(String batchID, List<BatchInput> batchInput, boolean waitabit) throws Exception {
		// Check if batchInput has at least one entry
		if (batchInput.isEmpty()) {
			throw new IllegalArgumentException("batchInput must have at least one entry");
		}

		// Check if all lineno values are greater than 0
		for (BatchInput input : batchInput) {
			if (input.getLine() <= 0) {
				throw new IllegalArgumentException("all lineno values must be greater than 0");
			}
		}

		// Retrieve batch record from the database
		Batches batch = batchjobSrv.getBatchByReqId(batchID);
		if (batch == null) {
			throw new IllegalArgumentException("Batch record not found");
		}

		// Check if the status of the batch is wait
		if (!batch.getStatus().equals("wait")) {
			throw new IllegalArgumentException("Batch status must be wait");
		}

		// Write records to batchrows
		for (BatchInput input : batchInput) {
			batchjobSrv.saveIntoBatchrows(batch, input.getLine(), input.getInput());
		}

		// Change the status of the batches record
		if (!waitabit) {
			batch.setStatus(BatchStatus.BatchQueued);
			Batches batches = batchjobSrv.saveIntoBatches(batch);
		}

		return new AlyaBatchResponse(batchID, batchInput.size());
	}

}
