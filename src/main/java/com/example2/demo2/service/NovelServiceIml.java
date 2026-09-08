package com.example2.demo2.service;

import com.example2.demo2.common.annotation.RequireAdmin;
import com.example2.demo2.common.exception.BusinessException;
import com.example2.demo2.dto.ChapterDTO;
import com.example2.demo2.dto.NovelDTO;
import com.example2.demo2.entity.Chapter;
import com.example2.demo2.entity.Novel;
import com.example2.demo2.repository.ChapterRepository;
import com.example2.demo2.repository.NovelRepository;
import com.example2.demo2.vo.NovelDetailVO;
import com.example2.demo2.vo.NovelRankVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 夏辰义
 * 2026/8/2618:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovelServiceIml implements INovelService{

    private final NovelRepository novelRepository;

    private final ChapterRepository chapterRepository;

    private final StringRedisTemplate stringRedisTemplate;

        // ========== 1. 创建小说 ==========
        @RequireAdmin
        public Novel addNovel(NovelDTO dto) {
            Novel novel = new Novel();
            novel.setTitle(dto.getTitle());
            novel.setSummary(dto.getSummary());
            novel.setCategory(dto.getCategory());
            novel.setCoverUrl(dto.getCoverUrl());
            novel.setStatus("ONGOING");   // 默认连载中
            novel.setTotalWords(0);
            return novelRepository.save(novel);
        }

        // ========== 2. 发布章节（核心） ==========
        @Transactional(rollbackFor = Exception.class)
        public Chapter addChapter(Integer novelId, ChapterDTO dto) {
            // 2.1 查小说存在
            Novel novel = novelRepository.findById(novelId)
                    .orElseThrow(() -> new BusinessException("小说不存在"));

            // 2.2 算这是第几章
            List<Chapter> list = chapterRepository.findByNovelIdOrderByChapterNumberAsc(novelId);
            int nextNumber = list.size() + 1;

            // 2.3 算字数（正文长度）
            int wordCount = dto.getContent().length();

            // 2.4 保存章节
            Chapter chapter = new Chapter();
            chapter.setNovelId(novelId);
            chapter.setChapterNumber(nextNumber);
            chapter.setTitle(dto.getTitle());
            chapter.setContent(dto.getContent());
            chapter.setWordCount(wordCount);
            chapterRepository.save(chapter);

            // 2.5 更新小说的总字数和最后更新时间
            novel.setTotalWords((novel.getTotalWords() == null ? 0 : novel.getTotalWords()) + wordCount);
            novel.setLastUpdateTime(LocalDateTime.now());
            novelRepository.save(novel);

            log.info("发布章节: novelId={}, chapterNo={}, title={}", novelId, nextNumber, dto.getTitle());
            return chapter;
        }

        // ========== 3. 修改章节 ==========
        @Transactional(rollbackFor = Exception.class)
        public Chapter updateChapter(Integer novelId, Integer chapterId, ChapterDTO dto) {
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new BusinessException("章节不存在"));

            if (!chapter.getNovelId().equals(novelId)) {
                throw new BusinessException("该章节不属于此小说");
            }

            // 重新算字数差
            int oldWords = chapter.getWordCount();
            int newWords = dto.getContent().length();
            int diff = newWords - oldWords;

            // 更新章节
            chapter.setTitle(dto.getTitle());
            chapter.setContent(dto.getContent());
            chapter.setWordCount(newWords);
            chapterRepository.save(chapter);

            // 更新小说总字数
            Novel novel = novelRepository.findById(novelId).orElseThrow();
            novel.setTotalWords(novel.getTotalWords() + diff);
            novelRepository.save(novel);

            return chapter;
        }

        // ========== 4. 删除章节 ==========
        @Transactional(rollbackFor = Exception.class)
        public void deleteChapter(Integer novelId, Integer chapterId) {
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new BusinessException("章节不存在"));

            if (!chapter.getNovelId().equals(novelId)) {
                throw new BusinessException("该章节不属于此小说");
            }

            // 扣减字数
            Novel novel = novelRepository.findById(novelId).orElseThrow();
            novel.setTotalWords(novel.getTotalWords() - chapter.getWordCount());
            novelRepository.save(novel);

            chapterRepository.delete(chapter);

            // 重排章节号（可选，但推荐做，和图书的 displayOrder 一样）
            List<Chapter> chapters = chapterRepository.findByNovelIdOrderByChapterNumberAsc(novelId);
            for (int i = 0; i < chapters.size(); i++) {
                chapters.get(i).setChapterNumber(i + 1);
            }
            chapterRepository.saveAll(chapters);
        }

        //========== 删除小说 ==========
        @Transactional(rollbackFor = Exception.class)
        public void deleteNovel(Integer id) {
            Novel novel = novelRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("小说不存在"));

            // 先删该小说的所有章节（避免外键或脏数据）
            chapterRepository.deleteByNovelId(id);

            // 再删小说
            novelRepository.delete(novel);

            log.info("删除小说: id={}, title={}", id, novel.getTitle());
        }
    // ========== 读者接口 ==========

    // 1. 小说列表（分页，按更新时间倒序）
    @Override
    public Page<Novel> findPage(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("lastUpdateTime").descending());
        return novelRepository.findAll(pageable);
    }


    // 2. 小说详情 + 章节目录
    @Transactional(readOnly = true)
    public NovelDetailVO findDetail(Integer novelId) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new BusinessException("小说不存在"));

        // 查出该小说的所有章节
        List<Chapter> chapters = chapterRepository.findByNovelIdOrderByChapterNumberAsc(novelId);

        // 组装 VO
        NovelDetailVO vo = new NovelDetailVO();
        vo.setId(novel.getId());
        vo.setTitle(novel.getTitle());
        vo.setSummary(novel.getSummary());
        vo.setCoverUrl(novel.getCoverUrl());
        vo.setCategory(novel.getCategory());
        vo.setStatus(novel.getStatus());
        vo.setTotalWords(novel.getTotalWords());
        vo.setLastUpdateTime(novel.getLastUpdateTime());

        // 把 Chapter 转成 ChapterOutline（去掉 content）
        List<NovelDetailVO.ChapterOutline> outlines = chapters.stream().map(c -> {
            NovelDetailVO.ChapterOutline o = new NovelDetailVO.ChapterOutline();
            o.setId(c.getId());
            o.setChapterNumber(c.getChapterNumber());
            o.setTitle(c.getTitle());
            o.setWordCount(c.getWordCount());
            o.setCreateTime(c.getCreateTime());
            return o;
        }).toList();

        vo.setChapters(outlines);
        return vo;
    }

    // 3. 阅读某一章（返回完整正文）
    @Transactional(readOnly = true)
    public Chapter readChapter(Integer novelId, Integer chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new BusinessException("章节不存在"));

        if (!chapter.getNovelId().equals(novelId)) {
            throw new BusinessException("该章节不属于此小说");
        }
        stringRedisTemplate.opsForZSet().incrementScore("ranking:novel:total",String.valueOf(novelId),1);
        return chapter;
    }

    //查询排行榜
    @Override
    public List<NovelRankVO> findRanking(int top){
            String rankKey = "ranking:novel:total";
            //从redis取数据
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet().reverseRangeWithScores(rankKey,0,top - 1);
        // 如果 Redis 里没有数据（刚上线），直接返回空列表，不要报错
        if (tuples == null || tuples.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 准备两个容器：一个存有序的 ID 列表，一个存 ID->热度的映射
        List<Integer> novelIds = new ArrayList<>();//用来保持顺序
        Map<Integer,Double> scoreMap = new HashMap<>();//用来放热度

        for (ZSetOperations.TypedTuple<String> tuple : tuples){
            Integer id = Integer.valueOf(tuple.getValue()); //拿到小说id
            Double score =tuple.getScore();//拿到热度分数
            novelIds.add(id);
            scoreMap.put(id,score);
        }
        // 3. 批量查数据库（拿到所有小说的详细信息）
        List<Novel> novels = novelRepository.findAllById(novelIds);
        Map<Integer,Novel> novelMap  = novels.stream()
                .collect(Collectors.toMap(
                        Novel::getId,
                        Function.identity()
                ));

        // 4. 按顺序遍历 novelIds，组装成 VO 列表
        List<NovelRankVO> result = new ArrayList<>();
        for (Integer id : novelIds) {
            Novel novel = novelMap.get(id);
            // 如果小说被删了，跳过（防止空指针）
            if (novel == null) {
                continue;
            }

            NovelRankVO vo = new NovelRankVO();
            vo.setNovelId(id);
            vo.setTitle(novel.getTitle());
            vo.setCoverUrl(novel.getCoverUrl());
            // 热度从 scoreMap 里拿，Double 转成 Long（因为前端看整数）
            vo.setHeat(scoreMap.get(id).longValue());

            result.add(vo);
        }
        return result;
    }
}
