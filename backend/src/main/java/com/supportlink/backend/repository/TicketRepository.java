package com.supportlink.backend.repository;

import com.supportlink.backend.domain.Ticket;
import com.supportlink.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser(User user);

    @Query("SELECT t FROM Ticket t WHERE (:status IS NULL OR t.status = :status) AND (:assigneeId IS NULL OR t.assignedAgent.agentId = :assigneeId)")
    List<Ticket> findAllWithFilters(@Param("status") Ticket.Status status, @Param("assigneeId") Long assigneeId);

    List<Ticket> findByAssignedAgentAndStatus(com.supportlink.backend.domain.Agent agent, Ticket.Status status);
}
