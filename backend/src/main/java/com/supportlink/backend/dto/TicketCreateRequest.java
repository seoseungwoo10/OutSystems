package com.supportlink.backend.dto;

import lombok.Data;

@Data
public class TicketCreateRequest {
    private String subject;
    private String priority; // Low, Medium, High
    private String content;
    private String category;
}
