package com.example.boardredis.controller.dto

data class PostCreateRequest(
    val title: String,
    val content: String,
    val createdBy: String,
)
