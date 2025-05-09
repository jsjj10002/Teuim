package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.dto.PostDto;
import com.terabyte.bapjigi.model.Post;
import com.terabyte.bapjigi.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDto postDto) {
        try {
            Post post = postService.createPost(postDto);
            return ResponseEntity.ok("게시물이 작성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(@PageableDefault(size = 10) Pageable pageable) {
        Page<PostDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<PostDto>> getMyPosts() {
        List<PostDto> posts = postService.getMyPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostDto> posts = postService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            PostDto postDto = postService.getPostById(id);
            return ResponseEntity.ok(postDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {
        try {
            Post updatedPost = postService.updatePost(id, postDto);
            return ResponseEntity.ok("게시물이 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
        try {
            Post post = postService.likePost(id);
            return ResponseEntity.ok("게시물에 좋아요를 표시했습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 