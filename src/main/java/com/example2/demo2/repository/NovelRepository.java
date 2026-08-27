package com.example2.demo2.repository;

import com.example2.demo2.entity.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository<Novel,Integer> {
}
