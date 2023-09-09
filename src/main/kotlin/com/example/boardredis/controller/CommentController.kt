package com.example.boardredis.controller

import com.example.boardredis.controller.dto.CommentCreateReqeust
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController {

    @PostMapping("posts/{postId}/comments")
    fun createComment(
        @PathVariable postId: Long,
        @RequestBody commentCreateRequest: CommentCreateReqeust,
    ): Long {
        println(commentCreateRequest.content)
        println(commentCreateRequest.createdBy)
        return 1L
    }
}
