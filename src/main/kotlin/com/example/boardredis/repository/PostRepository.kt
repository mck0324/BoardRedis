package com.example.boardredis.repository

import com.example.boardredis.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long> {
}

