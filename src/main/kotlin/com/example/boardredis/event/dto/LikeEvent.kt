package com.example.boardredis.event.dto

data class LikeEvent(
    val postId: Long,
    val createdBy: String,
)
