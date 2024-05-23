package com.remiges.alya.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.remiges.alya.jobs.BatchStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "batches")
// @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Batches {

	@Enumerated(EnumType.STRING)
	private BatchStatus status;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID Id;

	@Column(nullable = false)
	private String app;

	@Column(nullable = false)
	private String op;

	@Column(nullable = false)
	private char type;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(nullable = false, columnDefinition = "jsonb")
	private String context;

	@Column
	private String inputfile;

	@Column(nullable = false)
	private Timestamp reqat;

	@Column
	private Timestamp doneat;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, String> outputfiles;

	@Column
	private Integer nsuccess;

	@Column
	private Integer nfailed;

	@Column
	private Integer naborted;

}
