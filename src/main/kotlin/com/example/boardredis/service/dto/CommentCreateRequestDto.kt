package com.example.boardredis.service.dto

import com.example.boardredis.domain.Comment
import com.example.boardredis.domain.Post

data class CommentCreateRequestDto(
    val content: String,
    val createdBy: String,
)
fun CommentCreateRequestDto.toEntity(post: Post) = Comment(
    content = content,
    createdBy = createdBy,
    post = post,
)
