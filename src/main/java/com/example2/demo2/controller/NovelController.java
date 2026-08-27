package com.example2.demo2.controller;

import com.example2.demo2.common.Result;
import com.example2.demo2.common.annotation.RequireAdmin;
import com.example2.demo2.dto.ChapterDTO;
import com.example2.demo2.dto.NovelDTO;
import com.example2.demo2.entity.Chapter;
import com.example2.demo2.entity.Novel;
import com.example2.demo2.service.NovelService;
import com.example2.demo2.vo.NovelDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Scanner;

/**
 * 夏辰义
 * 2026/8/2618:18
 */
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "小说管理")
@RestController
@RequestMapping("/novels")
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    // ===== 管理员接口 =====

    @RequireAdmin
    @Operation(summary = "创建小说")
    @PostMapping
    public Result<Novel> addNovel(@RequestBody @Valid NovelDTO dto) {
        return Result.success(novelService.addNovel(dto));
    }

    @RequireAdmin
    @Operation(summary = "发布章节")
    @PostMapping("/{novelId}/chapters")
    public Result<Chapter> addChapter(
            @PathVariable Integer novelId,
            @RequestBody @Valid ChapterDTO dto) {
        return Result.success(novelService.addChapter(novelId, dto));
    }

    @RequireAdmin
    @Operation(summary = "修改章节")
    @PutMapping("/{novelId}/chapters/{chapterId}")
    public Result<Chapter> updateChapter(
            @PathVariable Integer novelId,
            @PathVariable Integer chapterId,
            @RequestBody @Valid ChapterDTO dto) {
        return Result.success(novelService.updateChapter(novelId, chapterId, dto));
    }

    @RequireAdmin
    @Operation(summary = "删除章节")
    @DeleteMapping("/{novelId}/chapters/{chapterId}")
    public Result<Void> deleteChapter(
            @PathVariable Integer novelId,
            @PathVariable Integer chapterId) {
        novelService.deleteChapter(novelId, chapterId);
        return Result.success();
    }

    @RequireAdmin
    @Operation(summary = "删除小说")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNovel(@PathVariable Integer id) {
        novelService.deleteNovel(id);
        return Result.success();
    }

    // ===== 读者接口（不加 @RequireAdmin）=====

    @Operation(summary = "小说列表")
    @GetMapping
    public Result<Page<Novel>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(novelService.findPage(page, size));
    }

    @Operation(summary = "小说详情")
    @GetMapping("/{id}")
    public Result<NovelDetailVO> detail(@PathVariable Integer id) {
        return Result.success(novelService.findDetail(id));
    }

    @Operation(summary = "阅读章节")
    @GetMapping("/{novelId}/chapters/{chapterId}")
    public Result<Chapter> readChapter(
            @PathVariable Integer novelId,
            @PathVariable Integer chapterId) {
        return Result.success(novelService.readChapter(novelId, chapterId));
    }
}