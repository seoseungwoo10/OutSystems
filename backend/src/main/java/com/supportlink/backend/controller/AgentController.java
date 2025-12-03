package com.supportlink.backend.controller;

import com.supportlink.backend.dto.AgentCreateRequest;
import com.supportlink.backend.dto.AgentResponse;
import com.supportlink.backend.dto.AgentUpdateRequest;
import com.supportlink.backend.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@Tag(name = "Agent Management", description = "상담원 관리 API (관리자 전용)")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상담원 등록")
    public ResponseEntity<AgentResponse> createAgent(@RequestBody AgentCreateRequest request) {
        AgentResponse response = agentService.createAgent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상담원 목록 조회")
    public ResponseEntity<List<AgentResponse>> getAllAgents(@RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(agentService.getAllAgents(isActive));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상담원 상세 조회")
    public ResponseEntity<AgentResponse> getAgent(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.getAgent(id));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상담원 정보 수정")
    public ResponseEntity<AgentResponse> updateAgent(@PathVariable Long id, @RequestBody AgentUpdateRequest request) {
        return ResponseEntity.ok(agentService.updateAgent(id, request));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상담원 계정 비활성화 (Soft Delete)")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
}
