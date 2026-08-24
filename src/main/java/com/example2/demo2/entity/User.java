package com.example2.demo2.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 夏辰义
 * 2026/8/1819:12
 */
@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false,length = 60)
    private String passwordHash;

    @Column(nullable = false)
    private String role = "USER";//默认是用户
}
