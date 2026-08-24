package com.example2.demo2.service;

import com.example2.demo2.entity.Book;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


/**
 * 夏辰义
 * 2026/8/1519:14
 */
@SpringBootTest
//启动整个 Spring Boot 应用，加载所有 Bean
@Transactional
//每个测试方法跑完后，数据库操作全部回滚"
public class BookServiceTest {

    @Autowired//Spring，请把 BookService 的实例注入给我"
    private BookService bookService;

    @Test
    void  testAdd(){
        //准备
        String name = "单元测试书";
        //执行
        Book book =bookService.add(name);
        //断言
        assertEquals("单元测试书",book.getName());
        assertNotNull(book.getId());
        assertTrue(book.getDisplayOrder() > 0);
    }

    @Test
    void testFindByIdNotFound(){
        Exception exception = assertThrows(RuntimeException.class,() -> {bookService.findById(99999);});
        assertTrue(exception.getMessage().contains("书不存在"));
    }

    @Test
    void testDelete(){
        //先加一本书
        Book book = bookService.add("待删除的书");
        Integer id = book.getId();
        //删除他
        bookService.delete(id);
        //断言
        assertThrows(RuntimeException.class,() -> {bookService.findById(id);});
    }
}
