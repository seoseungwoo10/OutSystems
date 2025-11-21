package com.supportlink.backend.repository;

import com.supportlink.backend.domain.Ticket;
import com.supportlink.backend.domain.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    List<TicketReply> findByTicket(Ticket ticket);
}
