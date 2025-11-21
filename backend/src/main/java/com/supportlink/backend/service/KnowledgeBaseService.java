package com.supportlink.backend.service;

import com.supportlink.backend.domain.KnowledgeBase;
import com.supportlink.backend.repository.KnowledgeBaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public KnowledgeBaseService(KnowledgeBaseRepository knowledgeBaseRepository) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }

    public List<KnowledgeBase> getFaqs(String category, String query) {
        return knowledgeBaseRepository.search(category, query);
    }

    @Transactional
    public KnowledgeBase getFaq(Long id) {
        KnowledgeBase faq = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        faq.setViewCount(faq.getViewCount() + 1);
        return faq;
    }

    public KnowledgeBase createFaq(KnowledgeBase faq) {
        return knowledgeBaseRepository.save(faq);
    }

    public KnowledgeBase updateFaq(Long id, KnowledgeBase faqDetails) {
        KnowledgeBase faq = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        faq.setTitle(faqDetails.getTitle());
        faq.setContent(faqDetails.getContent());
        faq.setCategory(faqDetails.getCategory());
        return knowledgeBaseRepository.save(faq);
    }

    public void deleteFaq(Long id) {
        knowledgeBaseRepository.deleteById(id);
    }
}
