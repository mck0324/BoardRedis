package com.example.boardredis.service.dto

import com.example.boardredis.domain.Post

data class PostDetailResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: String,
    val createdAt: String,
)

fun Post.toDetailResponseDto() = PostDetailResponseDto(
    id = id,
    title = title,
    content = content,
    createdBy = createdBy,
    createdAt = createdAt.toString()
)
