package com.supportlink.backend.repository;

import com.supportlink.backend.domain.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {

    List<KnowledgeBase> findByCategory(String category);

    @Query("SELECT k FROM KnowledgeBase k WHERE (:category IS NULL OR k.category = :category) AND (:query IS NULL OR k.title LIKE %:query% OR k.content LIKE %:query%)")
    List<KnowledgeBase> search(@Param("category") String category, @Param("query") String query);
}
