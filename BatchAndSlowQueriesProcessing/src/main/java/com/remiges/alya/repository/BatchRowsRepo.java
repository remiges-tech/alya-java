package com.remiges.alya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;

@Repository
public interface BatchRowsRepo extends JpaRepository<BatchRows, Long> {

    // Define custom query methods if needed...
    @Query(" SELECT b.app, b.op, b.Id, br.rowid, "
            + "  b.context, br.line, br.input FROM"
            + " BatchRows br INNER JOIN br.batch b "
            + " where br.batchStatus=:status")
    List<BatchJob> findAllWithBatches(@Param("status") String status);

}
