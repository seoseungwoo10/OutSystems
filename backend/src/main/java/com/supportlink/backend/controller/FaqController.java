package com.supportlink.backend.controller;

import com.supportlink.backend.domain.Agent;
import com.supportlink.backend.domain.KnowledgeBase;
import com.supportlink.backend.dto.FaqRequest;
import com.supportlink.backend.dto.FaqResponse;
import com.supportlink.backend.repository.AgentRepository;
import com.supportlink.backend.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@Tag(name = "FAQ", description = "FAQ API")
public class FaqController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final AgentRepository agentRepository;

    public FaqController(KnowledgeBaseService knowledgeBaseService, AgentRepository agentRepository) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.agentRepository = agentRepository;
    }

    @GetMapping
    @Operation(summary = "FAQ 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public List<FaqResponse> getFaqs(
            @Parameter(description = "카테고리 필터") @RequestParam(required = false) String category,
            @Parameter(description = "검색어") @RequestParam(required = false) String q) {
        return knowledgeBaseService.getFaqs(category, q).stream()
                .map(FaqResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "FAQ 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public FaqResponse getFaq(@PathVariable Long id) {
        return FaqResponse.from(knowledgeBaseService.getFaq(id));
    }

    @PostMapping
    @Operation(summary = "FAQ 생성 (관리자 전용)")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공")
    })
    public ResponseEntity<KnowledgeBase> createFaq(@RequestBody FaqRequest request, Authentication authentication) {
        String email = authentication.getName();
        Agent author = agentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        KnowledgeBase faq = new KnowledgeBase();
        faq.setTitle(request.getTitle());
        faq.setContent(request.getContent());
        faq.setCategory(request.getCategory());
        faq.setAuthor(author);

        return ResponseEntity.status(HttpStatus.CREATED).body(knowledgeBaseService.createFaq(faq));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeBase> updateFaq(@PathVariable Long id, @RequestBody FaqRequest request) {
        KnowledgeBase faqDetails = new KnowledgeBase();
        faqDetails.setTitle(request.getTitle());
        faqDetails.setContent(request.getContent());
        faqDetails.setCategory(request.getCategory());
        return ResponseEntity.ok(knowledgeBaseService.updateFaq(id, faqDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable Long id) {
        knowledgeBaseService.deleteFaq(id);
        return ResponseEntity.ok().build();
    }
}
