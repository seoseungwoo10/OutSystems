package com.supportlink.backend.dto;

import com.supportlink.backend.domain.KnowledgeBase;
import lombok.Data;

@Data
public class FaqResponse {
    private Long articleId;
    private String category;
    private String title;
    private String content;
    private int viewCount;
    private String authorName;

    public static FaqResponse from(KnowledgeBase kb) {
        FaqResponse response = new FaqResponse();
        response.setArticleId(kb.getArticleId());
        response.setCategory(kb.getCategory());
        response.setTitle(kb.getTitle());
        response.setContent(kb.getContent());
        response.setViewCount(kb.getViewCount());
        if (kb.getAuthor() != null) {
            response.setAuthorName(kb.getAuthor().getName());
        }
        return response;
    }
}
