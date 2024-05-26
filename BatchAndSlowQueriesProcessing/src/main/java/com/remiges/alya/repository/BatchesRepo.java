package com.remiges.alya.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;

@Repository
public interface BatchesRepo extends JpaRepository<Batches, UUID> {
	List<BatchRows> findByBatch(@Param("batch") Batches batch, Sort sort);

	Batches findByReqId(String reqId);

}
