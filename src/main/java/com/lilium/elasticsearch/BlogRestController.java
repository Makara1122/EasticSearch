package com.lilium.elasticsearch;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")

public class BlogRestController {

    private final BlogService blogService;

    @Autowired
    public BlogRestController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public Iterable<Blog> findAll() {
        return blogService.findAll();
    }

    @GetMapping("/search/{content}")
    public List<Blog> searchByContent(@PathVariable String content) {
        return blogService.searchByContent(content);
    }

    @PostMapping
    public void create(@RequestBody Blog blog) {
        blogService.saveBlog(blog);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        blogService.deleteById(id);
    }

    @GetMapping("/{id}")
    public Blog findById(@PathVariable String id) {
        return blogService.findById(id);
    }

}
