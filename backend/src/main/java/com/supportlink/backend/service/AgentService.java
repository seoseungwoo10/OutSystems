package com.supportlink.backend.service;

import com.supportlink.backend.domain.Agent;
import com.supportlink.backend.domain.Ticket;
import com.supportlink.backend.dto.AgentCreateRequest;
import com.supportlink.backend.dto.AgentResponse;
import com.supportlink.backend.dto.AgentUpdateRequest;
import com.supportlink.backend.repository.AgentRepository;
import com.supportlink.backend.repository.TicketRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AgentService {

    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public AgentService(AgentRepository agentRepository, TicketRepository ticketRepository, PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AgentResponse createAgent(AgentCreateRequest request) {
        if (agentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Agent agent = new Agent();
        agent.setEmail(request.getEmail());
        agent.setName(request.getName());
        agent.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        agent.setRole(request.getRole());
        agent.setIsActive(true);

        Agent savedAgent = agentRepository.save(agent);
        return AgentResponse.from(savedAgent);
    }

    @Transactional(readOnly = true)
    public List<AgentResponse> getAllAgents(Boolean isActive) {
        List<Agent> agents = agentRepository.findAll();
        if (isActive != null) {
            agents = agents.stream()
                    .filter(agent -> agent.getIsActive().equals(isActive))
                    .collect(Collectors.toList());
        }
        return agents.stream()
                .map(AgentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgentResponse getAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        return AgentResponse.from(agent);
    }

    public AgentResponse updateAgent(Long id, AgentUpdateRequest request) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));

        if (request.getName() != null) {
            agent.setName(request.getName());
        }
        if (request.getPassword() != null) {
            agent.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            agent.setRole(request.getRole());
        }

        Agent updatedAgent = agentRepository.save(agent);
        return AgentResponse.from(updatedAgent);
    }

    public void deleteAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));

        agent.setIsActive(false);
        agentRepository.save(agent);

        // Unassign open tickets
        List<Ticket> openTickets = ticketRepository.findByAssignedAgentAndStatus(agent, Ticket.Status.OPEN);
        for (Ticket ticket : openTickets) {
            ticket.setAssignedAgent(null);
            // Optionally change status to NEW or keep as OPEN but unassigned?
            // PRD says: 'Unassigned' 상태로 변경 (which implies assignedAgent = null).
            // It also says "change status to 'Unassigned'". But Status enum is NEW, OPEN, PENDING, CLOSED.
            // Usually 'Unassigned' is not a status but a property of assignment.
            // However, if the ticket was OPEN (meaning being worked on), and now unassigned, maybe it should go back to NEW?
            // Or stay OPEN but with no agent?
            // PRD says: "해당 상담원이 담당(assignee)하고 있던 'Open' 상태의 티켓은 'Unassigned' 상태로 변경하거나..."
            // 'Unassigned' is likely referring to the assignee field being null.
            // If the status enum doesn't have UNASSIGNED, I'll just set assignee to null.
            // I'll keep the status as OPEN or change to NEW.
            // If I look at Ticket.Status: NEW, OPEN, PENDING, CLOSED.
            // I will set assignedAgent to null.
            ticketRepository.save(ticket);
        }
    }
}
