package com.example.boardredis.controller.dto

import com.example.boardredis.service.dto.PostUpdateRequestDto

data class PostUpdateRequest(
    val title: String,
    val content: String,
    val updatedBy: String,
)

fun PostUpdateRequest.toDto() = PostUpdateRequestDto(
    title = title,
    content = content,
    updatedBy = updatedBy,
)
