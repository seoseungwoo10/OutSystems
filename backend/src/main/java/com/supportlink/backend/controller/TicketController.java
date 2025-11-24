package com.supportlink.backend.controller;

import com.supportlink.backend.domain.*;
import com.supportlink.backend.dto.ReplyRequest;
import com.supportlink.backend.dto.TicketCreateRequest;
import com.supportlink.backend.dto.TicketUpdateRequest;
import com.supportlink.backend.dto.TicketResponse;
import com.supportlink.backend.dto.TicketReplyResponse;
import com.supportlink.backend.repository.AgentRepository;
import com.supportlink.backend.repository.UserRepository;
import com.supportlink.backend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "티켓 API")
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;

    public TicketController(TicketService ticketService, UserRepository userRepository,
            AgentRepository agentRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
    }

    @PostMapping
    @Operation(summary = "티켓 생성")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공")
    })
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketCreateRequest request, Authentication authentication) {
        if (isAdminOrAgent(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setSubject(request.getSubject());
        ticket.setContent(request.getContent());
        ticket.setCategory(request.getCategory());
        ticket.setStatus(Ticket.Status.NEW);
        ticket.setPriority(Ticket.Priority.valueOf(request.getPriority().toUpperCase()));

        Ticket created = ticketService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponse.from(created));
    }

    @GetMapping
    @Operation(summary = "티켓 목록 조회")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<List<TicketResponse>> getTickets(
            @Parameter(description = "상태 필터") @RequestParam(required = false) String status,
            @Parameter(description = "담당자 ID 필터 (상담원 전용)") @RequestParam(required = false) Long assignee,
            Authentication authentication) {
        if (isAdminOrAgent(authentication)) {
            Ticket.Status ticketStatus = status != null ? Ticket.Status.valueOf(status.toUpperCase()) : null;
            List<TicketResponse> responses = ticketService.getAllTickets(ticketStatus, assignee).stream()
                    .map(TicketResponse::from)
                    .toList();
            return ResponseEntity.ok(responses);
        } else {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<TicketResponse> responses = ticketService.getTicketsForUser(user).stream()
                    .map(TicketResponse::from)
                    .toList();
            return ResponseEntity.ok(responses);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "티켓 상세 조회")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id, Authentication authentication) {
        Ticket ticket = ticketService.getTicket(id);
        if (!isAdminOrAgent(authentication)) {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!ticket.getUser().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    @SuppressWarnings("null")
    @PatchMapping("/{id}")
    @Operation(summary = "티켓 정보 수정 (상담원 전용)")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long id, @RequestBody TicketUpdateRequest request,
            Authentication authentication) {
        if (!isAdminOrAgent(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Ticket ticket = ticketService.getTicket(id);
        if (request.getStatus() != null) {
            ticket.setStatus(Ticket.Status.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getPriority() != null) {
            ticket.setPriority(Ticket.Priority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getAssigneeId() != null) {
            Agent agent = agentRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
            ticket.setAssignedAgent(agent);
        }

        Ticket updated = ticketService.updateTicket(ticket);
        return ResponseEntity.ok(TicketResponse.from(updated));
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "티켓 답변 목록 조회")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<List<TicketReplyResponse>> getReplies(@PathVariable Long id) {
        List<TicketReplyResponse> responses = ticketService.getReplies(id).stream()
                .map(TicketReplyResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/replies")
    @Operation(summary = "답변 작성")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공")
    })
    public ResponseEntity<TicketReplyResponse> createReply(@PathVariable Long id, @RequestBody ReplyRequest request,
            Authentication authentication) {
        Ticket ticket = ticketService.getTicket(id);
        boolean isAgent = isAdminOrAgent(authentication);
        Long authorId;
        TicketReply.AuthorType authorType;

        if (isAgent) {
            Agent agent = agentRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
            authorId = agent.getAgentId();
            authorType = TicketReply.AuthorType.AGENT;

            if (ticket.getStatus() == Ticket.Status.NEW) {
                ticket.setStatus(Ticket.Status.OPEN);
                ticketService.updateTicket(ticket);
            }
        } else {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!ticket.getUser().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            authorId = user.getUserId();
            authorType = TicketReply.AuthorType.USER;
        }

        TicketReply reply = new TicketReply();
        reply.setTicket(ticket);
        reply.setMessage(request.getMessage());
        reply.setAuthorId(authorId);
        reply.setAuthorType(authorType);

        TicketReply created = ticketService.createReply(reply);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketReplyResponse.from(created));
    }

    private boolean isAdminOrAgent(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_AGENT")) ||
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
