package com.remiges.alya.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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

	@Column(nullable = false)
	@Lob
	private String context;

	@Column
	private String inputfile;

	@Column(nullable = false)
	private LocalDateTime reqat;

	@Column
	private LocalDateTime doneat;

	@Column
	private String outputfiles;

	@Column
	private Integer nsuccess;

	@Column
	private Integer nfailed;

	@Column
	private Integer naborted;

}
