package com.example2.demo2.repository;

import com.example2.demo2.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter,Integer> {
    List<Chapter> findByNovelIdOrderByChapterNumberAsc(Integer novelId);

    void deleteByNovelId(Integer novelId);
}
