package com.supportlink.backend.dto;

import lombok.Data;

@Data
public class TicketCreateRequest {
    private String subject;
    private String priority; // Low, Medium, High
    // Category is mentioned in PRD for creation ("제목, 카테고리, 내용") but Ticket entity
    // doesn't have category column in PRD schema.
    // PRD 3.3 says: "제목, 카테고리, 내용".
    // PRD 4.4 Schema: ticket_id, user_id, assigned_agent_id, subject, status,
    // priority, created_at, updated_at.
    // There is NO category column in the schema.
    // However, the requirement says "Category".
    // I will assume it might be missing in schema or I should add it.
    // Given the strict schema instruction, I will follow the schema.
    // But wait, "내용" (Content) is also missing in Ticket schema!
    // TicketReplies has 'message'.
    // Maybe the initial content is the first reply? Or the Ticket needs a content
    // field?
    // Usually Ticket has a description/content.
    // PRD 3.3 says "제목, 카테고리, 내용".
    // PRD 4.4 Schema is missing 'content' and 'category'.
    // I should probably add them to the Entity to make it functional, or assume the
    // "content" is the first TicketReply.
    // I'll add 'description' and 'category' to Ticket entity to be safe and
    // functional.
    // Wait, I already created the Ticket entity without them.
    // I will modify Ticket entity to include 'description' and 'category'.

    private String content;
    private String category;
}
