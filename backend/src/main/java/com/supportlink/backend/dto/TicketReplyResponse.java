package com.supportlink.backend.dto;

import com.supportlink.backend.domain.TicketReply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TicketReplyResponse {

    private Long replyId;
    private Long ticketId;
    private Long authorId;
    private String authorType;
    private String message;
    private LocalDateTime createdAt;

    public static TicketReplyResponse from(TicketReply reply) {
        return TicketReplyResponse.builder()
                .replyId(reply.getReplyId())
                .ticketId(reply.getTicket() != null ? reply.getTicket().getTicketId() : null)
                .authorId(reply.getAuthorId())
                .authorType(reply.getAuthorType() != null ? reply.getAuthorType().name() : null)
                .message(reply.getMessage())
                .createdAt(reply.getCreatedAt())
                .build();
    }
}

