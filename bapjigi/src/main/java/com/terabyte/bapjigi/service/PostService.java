package com.terabyte.bapjigi.service;

import com.terabyte.bapjigi.dto.PostDto;
import com.terabyte.bapjigi.model.Post;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional
    public Post createPost(PostDto postDto) {
        User currentUser = userService.getCurrentUser();
        
        Post post = Post.builder()
                .user(currentUser)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .imageUrl(postDto.getImageUrl())
                .viewCount(0)
                .likeCount(0)
                .build();
        
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getMyPosts() {
        User currentUser = userService.getCurrentUser();
        List<Post> posts = postRepository.findByUser(currentUser);
        
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PostDto> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return posts.map(this::convertToDto);
    }

    @Transactional
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        
        // 조회수 증가
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        
        return convertToDto(post);
    }

    @Transactional
    public Post updatePost(Long id, PostDto postDto) {
        User currentUser = userService.getCurrentUser();
        
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("자신의 게시물만 수정할 수 있습니다.");
        }
        
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        if (postDto.getImageUrl() != null) {
            post.setImageUrl(postDto.getImageUrl());
        }
        
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        User currentUser = userService.getCurrentUser();
        
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("자신의 게시물만 삭제할 수 있습니다.");
        }
        
        postRepository.delete(post);
    }
    
    @Transactional
    public Post likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));
        
        post.setLikeCount(post.getLikeCount() + 1);
        return postRepository.save(post);
    }

    private PostDto convertToDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(PostDto.UserInfoDto.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .profileName(post.getUser().getProfileName())
                        .profileImage(post.getUser().getProfileImage())
                        .build())
                .build();
    }
} 