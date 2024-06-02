package com.remiges.alya.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.remiges.alya.entity.Batches;

@Repository
public interface BatchesRepo extends JpaRepository<Batches, UUID> {
//	List<Batches> findByBatch(Batch batch, Sort sort);

	Batches findByReqat(Timestamp reqat);

	List<Batches> findByAppAndOpAndReqatAfter(String app, String op, LocalDateTime thresholdTime);

	List<Batches> findByIdAndType(UUID id, char type);

	List<Batches> findByTypeAndAppAndOpAndReqatAfter(char type, String app, String op, LocalDateTime thresholdTime);

	Optional<Batches> findById(UUID id);

}
