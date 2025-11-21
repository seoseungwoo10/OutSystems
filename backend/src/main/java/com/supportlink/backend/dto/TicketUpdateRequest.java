package com.supportlink.backend.dto;

import lombok.Data;

@Data
public class TicketUpdateRequest {
    private String status;
    private String priority;
    private Long assigneeId;
}
