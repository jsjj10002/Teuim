package com.terabyte.bapjigi.repository;

import com.terabyte.bapjigi.model.Post;
import com.terabyte.bapjigi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    List<Post> findByUser(User user);
    Page<Post> findByUser(User user, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
} 