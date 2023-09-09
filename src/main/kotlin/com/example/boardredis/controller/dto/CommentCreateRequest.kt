package com.example.boardredis.controller.dto

data class CommentCreateRequest(
    val content: String,
    val createdBy: String,
)
