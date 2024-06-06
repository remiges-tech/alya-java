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

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, String> res;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, String> blobrows;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, String> messages;

	@Column
	private String doneby;

	public Long getRowid() {
		return rowid;
	}

	public void setRowid(Long rowid) {
		this.rowid = rowid;
	}

	public Batches getBatch() {
		return batch;
	}

	public void setBatch(Batches batch) {
		this.batch = batch;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public BatchStatus getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(BatchStatus batchStatus) {
		this.batchStatus = batchStatus;
	}

	public Timestamp getReqat() {
		return reqat;
	}

	public void setReqat(Timestamp reqat) {
		this.reqat = reqat;
	}

	public Timestamp getDoneat() {
		return doneat;
	}

	public void setDoneat(Timestamp doneat) {
		this.doneat = doneat;
	}

	public Map<String, String> getRes() {
		return res;
	}

	public void setRes(Map<String, String> res) {
		this.res = res;
	}

	public Map<String, String> getBlobrows() {
		return blobrows;
	}

	public void setBlobrows(Map<String, String> blobrows) {
		this.blobrows = blobrows;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	public String getDoneby() {
		return doneby;
	}

	public void setDoneby(String doneby) {
		this.doneby = doneby;
	}

	public Long getRowId() {
		return rowid;
	}

}
