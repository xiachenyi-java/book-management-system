package com.example2.demo2.service;

import com.example2.demo2.dto.ChapterDTO;
import com.example2.demo2.dto.NovelDTO;
import com.example2.demo2.entity.Chapter;
import com.example2.demo2.entity.Novel;
import com.example2.demo2.vo.NovelDetailVO;
import org.springframework.data.domain.Page;

public interface INovelService {
    // ========== 1. 创建小说 ==========
     Novel addNovel(NovelDTO dto);

    // ========== 2. 发布章节（核心） ==========
     Chapter addChapter(Integer novelId, ChapterDTO dto);

    // ========== 3. 修改章节 ==========
     Chapter updateChapter(Integer novelId, Integer chapterId, ChapterDTO dto);

    // ========== 4. 删除章节 ==========
     void deleteChapter(Integer novelId, Integer chapterId);

    //========== 删除小说 ==========
     void deleteNovel(Integer id);

    // 1. 小说列表（分页，按更新时间倒序）
     Page<Novel> findPage(int page, int size);

    // 2. 小说详情 + 章节目录
     NovelDetailVO findDetail(Integer novelId);

    // 3. 阅读某一章（返回完整正文）
     Chapter readChapter(Integer novelId, Integer chapterId);
}
