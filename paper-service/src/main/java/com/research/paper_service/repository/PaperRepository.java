package com.research.paper_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.research.paper_service.entity.Paper;

public interface PaperRepository extends JpaRepository<Paper,Long> {

} 