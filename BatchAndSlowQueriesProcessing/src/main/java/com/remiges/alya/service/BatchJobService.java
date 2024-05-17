package com.remiges.alya.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.repository.BatchRowsRepo;
import com.remiges.alya.repository.BatchesRepo;

import jakarta.transaction.Transactional;

import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.jobs.BatchStatus;

@Service
public class BatchJobService {

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

    public void UpdateBatchRowStatus(Long rowid, String batchStatus) throws Exception {

        Optional<BatchRows> batchrow = batchrowrepo.findById(rowid);
        if (batchrow.isPresent()) {
            batchrow.get().setBatchStatus(batchStatus);
            batchrowrepo.save(batchrow.get());

        } else {
            throw new Exception("batch row not found" + rowid);
        }

    }

    @Transactional
    public void SwitchBatchToInprogress(List<BatchJob> queuedbatchrows, BatchStatus status) {

        queuedbatchrows.stream().forEach(batchrow -> {
            try {
                UpdateBatchRowStatus(batchrow.getrowid(), status.toString());

                UpdateBatchStatus(batchrow.getbatch(), status);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

    }

    public void UpdateBatchStatus(UUID batchId, BatchStatus Status) throws Exception {
        Optional<Batches> batchrec = batchesRepo.findById(batchId);
        if (batchrec.isPresent()) {
            batchrec.get().setStatus(Status);
            batchesRepo.save(batchrec.get());

        } else {
            throw new Exception("batch not found" + batchId);
        }

    }

    /*
     * public void SaveSlowQueryBatchJobs(BatchRequestDto requestDto) {
     * 
     * BatchJobs batchJobs = mapper.convertValue(requestDto, BatchJobs.class);
     * batchJobs.setType('Q');
     * batchJobs.setReqat(Timestamp.from(Instant.now()));
     * batchJobs.setStatus(BatchStatus.queued);
     * BatchRow batchRow = new BatchRow();
     * batchRow.setBatch(batchJobs);
     * batchRow.setLine(0);
     * batchRow.setStatus(BatchStatus.queued);
     * batchRow.setReqat(Timestamp.from(Instant.now()));
     * 
     * batchjobsrepo.save(batchJobs);
     * 
     * }
     */

    public void SaveBatchRows() {

    }

}
