package com.example2.demo2.service;

import com.example2.demo2.entity.Book;
import com.example2.demo2.entity.User;
import com.example2.demo2.repository.BookRepository;
import com.example2.demo2.repository.UserRepository;
import com.example2.demo2.vo.BookPageVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 夏辰义
 * 2026/8/1020:50
 */
@Slf4j
//@Slf4j 会自动给这个类生成一个 log 对象，你直接用 log.info(...) 就行
@Service
//只穿这一件衣服，不要穿 @RestController
@RequiredArgsConstructor
//自动为所有 final 字段生成构造器。
public class BookService {


    private final BookRepository bookRepository;
    private final UserService userService;

    // 获取所有书
    public List<Book> findAll() {
        return bookRepository.findAllByOrderByDisplayOrderAsc();
        //方法名不叫 getAll，叫 findAll。因为 Service 不关心 HTTP，它只关心"查找"
    }

    // 加书
    @CacheEvict(value = "books", allEntries = true)
    public Book add(String name) {// ← 只接收一个 String，不接收 Book 实体
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        log.info("用户 [{}] 添加图书: name={}", currentUser.getUsername(), name);
        log.info("添加图书: name={}",name);
        Integer max = bookRepository.findMaxDisplayOrder();
        Book book = new Book();
        book.setName(name);
        book.setDisplayOrder(max == null ? 1 : max + 1);
        return bookRepository.save(book);
    }

    // 修改书
    @CacheEvict(value = "books", allEntries = true)
    public Book update(Integer id ,String name){
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("书不存在"));//找到书
        //bookRepository.findById(id) 返回什么？
        //返回的是 Optional<Book>，不是直接返回 Book
        //.orElseThrow(...) 是什么意思？
        //"如果盒子是空的，就抛出异常；如果有书，就把书拿出来。
        book.setName(name);
        return bookRepository.save(book);//返回改好的书
    }

    //删除书
    @CacheEvict(value = "books", allEntries = true)
    @Transactional//给删除加一个事务，防止删除一半数据库崩了
    public void delete(Integer id){
        log.info("删除图书: id={}",id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("书不存在"));
        bookRepository.delete(book);

        // 重排序号
        List<Book> books = bookRepository.findAllByOrderByDisplayOrderAsc();
        for (int i = 0; i < books.size(); i++) {
            books.get(i).setDisplayOrder(i + 1);
        }
        bookRepository.saveAll(books);  // ← 用 saveAll 代替循环 save

        //========临时事务回滚========
        //throw new RuntimeException("测试事务回滚");
        //==========================
    }

    //查书
    public Book findById(Integer id) {
        log.debug("查询图书: id={}", id);  // ← 用 debug 级别
        //原因：后者在日志级别设为 WARN 时，字符串拼接已经执行了，
        // 浪费性能。前者用占位符 {}，只有真正打印时才拼接
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("书不存在"));
    }

    //模糊查询
    public List<Book> search(String keyword){
        return bookRepository.findByNameContaining(keyword);
    }

    //分页
    @Cacheable(value = "books", key = "#pageNum + '-' + #pageSize")
    public BookPageVO getBooks(int pageNum, int pageSize) {
        Page<Book> page = bookRepository.findAll(
                PageRequest.of(pageNum - 1, pageSize)
        );
        return BookPageVO.of(page);
    }

}
