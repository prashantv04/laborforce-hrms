package com.example.HRMS.demo.site.repository;

import com.example.HRMS.demo.site.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
}