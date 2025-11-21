package com.supportlink.backend.dto;

import com.supportlink.backend.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TicketResponse {

    private Long ticketId;
    private Long userId;
    private Long assignedAgentId;
    private String subject;
    private String content;
    private String category;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TicketResponse from(Ticket ticket) {
        return TicketResponse.builder()
                .ticketId(ticket.getTicketId())
                .userId(ticket.getUser() != null ? ticket.getUser().getUserId() : null)
                .assignedAgentId(ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getAgentId() : null)
                .subject(ticket.getSubject())
                .content(ticket.getContent())
                .category(ticket.getCategory())
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .priority(ticket.getPriority() != null ? ticket.getPriority().name() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}

