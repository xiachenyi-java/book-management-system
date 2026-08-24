package com.example2.demo2.vo;

import com.example2.demo2.entity.Book;
import lombok.Data;

import java.util.List;


/**
 * 夏辰义
 * 2026/8/2415:05
 */
@Data
public class BookPageVO {
    private List<Book> list;        // 当前页数据
    private long total;             // 总条数
    private int pageNum;            // 当前页码（从 1 开始）
    private int pageSize;           // 每页大小
    private int totalPages;         // 总页数

    public static BookPageVO of(org.springframework.data.domain.Page<Book> page) {
        BookPageVO vo = new BookPageVO();
        vo.setList(page.getContent());
        vo.setTotal(page.getTotalElements());
        vo.setPageNum(page.getNumber() + 1);    // Spring 从 0 开始，前端习惯从 1 开始
        vo.setPageSize(page.getSize());
        vo.setTotalPages(page.getTotalPages());
        return vo;
    }
}
