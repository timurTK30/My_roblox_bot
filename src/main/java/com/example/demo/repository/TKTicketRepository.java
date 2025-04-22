package com.example.demo.repository;

import com.example.demo.domain.TKTicket;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TKTicketRepository extends JpaRepository<TKTicket, Long> {
    Optional<TKTicket> getTKBallByUser(User user);
    void deleteByUser(User user);
}
