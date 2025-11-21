package com.supportlink.backend.dto;

import lombok.Data;

@Data
public class FaqRequest {
    private String title;
    private String content;
    private String category;
}
