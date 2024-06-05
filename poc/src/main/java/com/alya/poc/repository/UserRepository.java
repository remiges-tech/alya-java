package com.alya.poc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.alya.poc.model.AlyaEntity;

@Repository
public interface UserRepository extends JpaRepository<AlyaEntity, Long>{

    Optional<AlyaEntity> findById(Long id);

    AlyaEntity save(AlyaEntity alyaEntity);


}
