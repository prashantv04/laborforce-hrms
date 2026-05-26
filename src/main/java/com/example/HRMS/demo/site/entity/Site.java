package com.example.HRMS.demo.site.entity;

import com.example.HRMS.demo.common.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sites")
public class Site extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", nullable = false, length = 150)
    private String siteName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean active = true;
}