package com.supportlink.backend.service;

import com.supportlink.backend.domain.Ticket;
import com.supportlink.backend.domain.TicketReply;
import com.supportlink.backend.domain.User;
import com.supportlink.backend.repository.TicketReplyRepository;
import com.supportlink.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;

    public TicketService(TicketRepository ticketRepository, TicketReplyRepository ticketReplyRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketReplyRepository = ticketReplyRepository;
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getTicketsForUser(User user) {
        return ticketRepository.findByUser(user);
    }

    public List<Ticket> getAllTickets(Ticket.Status status, Long assigneeId) {
        return ticketRepository.findAllWithFilters(status, assigneeId);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public Ticket updateTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public List<TicketReply> getReplies(Long ticketId) {
        Ticket ticket = getTicket(ticketId);
        return ticketReplyRepository.findByTicket(ticket);
    }

    public TicketReply createReply(TicketReply reply) {
        return ticketReplyRepository.save(reply);
    }
}
