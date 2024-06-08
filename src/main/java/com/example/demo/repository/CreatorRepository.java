package com.example.demo.repository;

import com.example.demo.domain.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface CreatorRepository extends JpaRepository<Creator, Long> {
}
