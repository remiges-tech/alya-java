package com.remiges.alya.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.repository.BatchRowsRepo;
import com.remiges.alya.repository.BatchesRepo;

import jakarta.transaction.Transactional;
import lombok.NonNull;

import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchStatus;
import com.remiges.alya.jobs.SummaryUtils;

@Service
public class BatchJobService {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);


    ObjectMapper mapper;

    private BatchRowsRepo batchrowrepo;

    BatchesRepo batchesRepo;

    BatchJobService(ObjectMapper mapper, BatchRowsRepo batchrowrepo, BatchesRepo batchesRepo) {

        this.batchesRepo = batchesRepo;
        this.batchrowrepo = batchrowrepo;
        this.mapper = mapper;
    }

    public List<BatchJob> getAllQueuedBatchRow(BatchStatus status) {
        return batchrowrepo.findAllWithBatches(status);
    }

    public void UpdateBatchRowStatus(Long rowid, BatchStatus batchStatus) throws Exception {

        Optional<BatchRows> batchrow = batchrowrepo.findById(rowid);
        if (batchrow.isPresent()) {
            batchrow.get().setBatchStatus(batchStatus);
            batchrowrepo.save(batchrow.get());

        } else {
            String erlog = "batch row not found" + rowid;
            logger.debug(erlog);
            throw new Exception(erlog);
        }

    }

    public void UpdateBatchStatus(UUID batchId, BatchStatus Status) throws Exception {
        Optional<Batches> batchrec = batchesRepo.findById(batchId);
        if (batchrec.isPresent()) {
            batchrec.get().setStatus(Status);
            batchesRepo.save(batchrec.get());

        } else {
            String erlog = "batch not found for batchid" + batchId;
            logger.debug(erlog);
            throw new Exception(erlog);
        }

    }

    public void UpdateBatchSummary(UUID batchId, BatchStatus Status, Integer nfailed, Integer nsuccess, Integer nabort,
            String outputfiles) throws Exception {
        Optional<Batches> batchrec = batchesRepo.findById(batchId);
        if (!batchrec.isPresent()) {
            Batches batch = batchrec.get();
            batch.setStatus(Status);
            batch.setDoneat(Timestamp.from(Instant.now()));
            batch.setNaborted(nabort);
            batch.setNfailed(nfailed);
            batch.setNsuccess(nsuccess);
            batch.setOutputfiles(outputfiles);

            batchesRepo.save(batch);

        } else {
            String erlog = "batch not found for batchid" + batchId;
            logger.debug(erlog);
            throw new Exception(erlog);
        }
    }

    @Transactional
    public void SwitchBatchToInprogress(List<BatchJob> queuedbatchrows, BatchStatus status) {

        queuedbatchrows.stream().forEach(batchrow -> {
            try {
                UpdateBatchRowStatus(batchrow.getrowid(), status);

                UpdateBatchStatus(batchrow.getbatch(), status);

            } catch (Exception e) {
                String erlog = "exception while updating batch status ex = " + e.getMessage();
                logger.debug(erlog);

            }
        });

    }

    public void updateBatchRowForSlowQueryoutput(BatchJob rowtoproces, BatchOutput batchOutput) {

        Optional<BatchRows> sqRow = batchrowrepo.findById(rowtoproces.getrowid());

        if (sqRow.isPresent()) {
            BatchRows batchRows = sqRow.get();
            batchRows.setDoneat(Timestamp.from(Instant.now()));
            batchRows.setBatchStatus(batchOutput.getStatus());
            batchRows.setRes(batchOutput.getResult());
            batchRows.setMessages(batchOutput.getMessages());
            batchrowrepo.save(batchRows);
        }
    }

    public void updateBatchRowForBatchOutput(BatchJob rowtoproces, BatchOutput batchOutput) {
        Optional<BatchRows> sqRow = batchrowrepo.findById(rowtoproces.getrowid());

        if (sqRow.isPresent()) {
            BatchRows batchRows = sqRow.get();
            batchRows.setDoneat(Timestamp.from(Instant.now()));
            batchRows.setBatchStatus(batchOutput.getStatus());
            batchRows.setRes(batchOutput.getResult());
            batchRows.setMessages(batchOutput.getMessages());
            batchRows.setBlobrows(batchOutput.getBlobRows());

            batchrowrepo.save(batchRows);
        }
    }

    /*
     * public void SaveSlowQueryBatchJobs(BatchRequestDto requestDto) {
     * 
     * Batches batchJobs = mapper.convertValue(requestDto, Batches.class);
     * batchJobs.setType('Q');
     * batchJobs.setReqat(Timestamp.from(Instant.now()));
     * batchJobs.setStatus(BatchStatus.BatchQueued);
     * BatchRows batchRow = new BatchRows();
     * batchRow.setBatch(batchJobs);
     * batchRow.setLine(0);
     * batchRow.setBatchStatus(BatchStatus.BatchQueued);
     * batchRow.setReqat(Timestamp.from(Instant.now()));
     * 
     * batchesRepo.save(batchJobs);
     * 
     * }
     */

    public void SaveBatchRows() {

    }

    public List<BatchRows> GetBatchRowsByBatchIDSorted(Batches batch) {

        return batchrowrepo.findByBatch(batch, Sort.by(Sort.Direction.ASC, "line"));
    }

    public String SummarizeBatch(UUID batchId) {
        Optional<Batches> opbatch = batchesRepo.findById(batchId);

        if (!opbatch.isPresent())
            return "Batch not found";

        Batches batch = opbatch.get();
        if (batch.getDoneat() != null) {
            return "";
        }

        Integer count = batchrowrepo.countBatchRowsByBatchIDAndStatus(batch,
                List.of(BatchStatus.BatchInProgress, BatchStatus.BatchQueued));

        if (count > 0) {
            return "";
        }

        // Fetch all batchrows records for the batch, sorted by "line"
        List<BatchRows> batchrows = batchrowrepo.findByBatch(batch, Sort.by(Sort.Direction.ASC, "line"));

        SummaryUtils sumutils = new SummaryUtils();
        sumutils.calculateSummaryCounters(batchrows);

        sumutils.determineBatchStatus();

        List<BatchRows> processedBatchRows = batchrowrepo.GetProcessedBatchRowsByBatchIDSortedRow(batch,
                List.of(BatchStatus.BatchSuccess, BatchStatus.BatchFailed));

        //// Create temporary files for each unique logical file in blobrows
        // Append blobrows strings to the appropriate temporary files

        // Move temporary files to the object store and update outputfiles

        // Update the batches record with summarized information
        try {
            UpdateBatchSummary(batchId, sumutils.getSummaryStatus(), sumutils.getNfailed(), sumutils.getNSuccess(),
                    sumutils.getNSuccess(), null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";

    }
}
