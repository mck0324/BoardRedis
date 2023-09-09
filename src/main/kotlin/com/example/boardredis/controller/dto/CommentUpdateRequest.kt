package com.example.boardredis.controller.dto

data class CommentUpdateRequest(
    val content: String,
    val updatedBy: String,
)
