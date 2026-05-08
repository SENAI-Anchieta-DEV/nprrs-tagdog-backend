package com.senai.nprrs_tagdog_backend.domain.repository;

import com.senai.nprrs_tagdog_backend.domain.entity.Animal;
import com.senai.nprrs_tagdog_backend.domain.entity.CheckInCheckOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInCheckOutRepository extends JpaRepository<CheckInCheckOut, String> {
}
