package com.example.boardredis.repository

import com.example.boardredis.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository: JpaRepository<Tag, Long> {
    fun findByPostId(postId: Long): List<Tag>
}
