package com.remiges.alya.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.jobs.BatchStatus;

@Repository
public interface BatchRowsRepo extends JpaRepository<BatchRows, Long> {

	// Define custom query methods if needed...
	@Query(" SELECT new  com.remiges.alya.entity.BatchJob( b.app, b.op, b.Id, br.rowid, "
			+ "  b.context, br.line, br.input ) FROM" + " BatchRows br INNER JOIN br.batch b "
			+ " where br.batchStatus=:status ")
	List<BatchJob> findAllWithBatches(@Param("status") BatchStatus status, Pageable page);

	List<BatchRows> findByBatch(@Param("batch") Batches batch, Sort sort);

	@Query("SELECT COUNT(*) FROM BatchRows br WHERE br.batch = :batch AND br.batchStatus IN :properties")
	Integer countBatchRowsByBatchIDAndStatus(@Param("batch") Batches batch,
			@Param("properties") List<BatchStatus> properties);

	@Query("FROM BatchRows WHERE batch = :batch AND batchStatus IN :properties ORDER BY line")
	List<BatchRows> GetProcessedBatchRowsByBatchIDSortedRow(@Param("batch") Batches batch,
			@Param("properties") List<BatchStatus> properties);

}
