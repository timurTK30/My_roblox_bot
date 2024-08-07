package com.example.demo.repository;

import com.example.demo.domain.SupportMassage;
import com.example.demo.dto.SuportMassageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface SupportMassageRepository extends JpaRepository<SupportMassage, Long> {
    Optional<SupportMassage> getMassageByChatId(Long chatId);
}
