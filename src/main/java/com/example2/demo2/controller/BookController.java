package com.example2.demo2.controller;

import com.example2.demo2.common.RequireAdmin;
import com.example2.demo2.dto.BookDTO;
import com.example2.demo2.entity.Book;
import com.example2.demo2.entity.User;
import com.example2.demo2.service.BookService;
import com.example2.demo2.common.Result;
import com.example2.demo2.service.UserService;
import com.example2.demo2.vo.BookPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@SecurityRequirement(name = "BearerAuth")
@Tag(name = "图书管理")
@RestController
/*
组合注解，相当于 @Controller + @ResponseBody。
表示这个类的所有方法返回的数据直接写入 HTTP 响应体（返回 JSON，而不是跳转页面）。
 */
@RequestMapping("/books")
//给这个控制器设置一个基础路径。下面所有接口的 URL 前面都要加上 /books。
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final UserService userService;


    // 获取所有书
    @Operation(summary = "获取图书列表")
    @GetMapping("/books")
    public Result<BookPageVO> getBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return Result.success(bookService.getBooks(page, size));
    }

    // 找一本书
    @Operation(summary = "找一本书")
    @GetMapping("/{id}")
    public Result<Book> getOne(@PathVariable Integer id) {
      return Result.success(bookService.findById(id));
    }

    //模糊查询
    @Operation(summary = "搜索图书")
    @GetMapping("/search")
    public Result<List<Book>> search(@RequestParam String keyword){
        return Result.success(bookService.search(keyword));
    }

    // 加书
    @RequireAdmin
    @Operation(summary = "添加图书")
    @PostMapping
    public Result<Book> add(@RequestBody @Valid BookDTO book) {
     Book saved = bookService.add(book.getName());
     return Result.success(saved);
    }

    // 修改书
    @RequireAdmin
    @Operation(summary = "修改图书")
    @PutMapping("/{id}")
    public Result<Book> update(@PathVariable Integer id, @RequestBody @Valid BookDTO book) {
        return Result.success(bookService.update(id, book.getName()));
    }

    // 删除书
    @RequireAdmin
    @Operation(summary = "删除图书")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        bookService.delete(id);
        return Result.success();
    }
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

}