package com.example.boardredis.service.dto

data class CommentUpdateRequestDto(
    val content: String,
    val updatedBy: String,
)
