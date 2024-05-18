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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.repository.BatchRowsRepo;
import com.remiges.alya.repository.BatchesRepo;

import jakarta.transaction.Transactional;

import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchStatus;

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

    public List<BatchJob> getAllQueuedBatchRow(String status) {
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

    public void updateBatchRowSlowQueryoutput(BatchJob rowtoproces, BatchOutput batchOutput) {

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

    public void updateBatchOutput() {

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

}
