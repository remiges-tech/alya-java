package com.remiges.alya.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.remiges.alya.entity.Batches;

@Repository
public interface BatchesRepo extends JpaRepository<Batches, UUID> {

}
