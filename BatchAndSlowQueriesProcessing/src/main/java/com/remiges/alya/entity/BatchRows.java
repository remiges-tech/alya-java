package com.remiges.alya.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.remiges.alya.jobs.BatchStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "batchrows")
public class BatchRows {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rowid;

	@ManyToOne
	@JoinColumn(name = "batch_id", nullable = false)
	private Batches batch;

	@Column(nullable = false)
	private Integer line;

	@Column(nullable = false)
	@Lob
	private String input;

	@Column(nullable = false)
	private BatchStatus batchStatus;

	@Column(nullable = false)
	private Timestamp reqat;

	@Column
	private Timestamp doneat;

	@Column
	private String res;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, String> blobrows;

	@Column
	@Lob
	private String messages;

	@Column
	private String doneby;

}
