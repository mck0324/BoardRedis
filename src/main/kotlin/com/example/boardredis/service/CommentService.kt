package com.example.boardredis.service

import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.repository.CommentRepository
import com.example.boardredis.repository.PostRepository
import com.example.boardredis.service.dto.CommentCreateRequestDto
import com.example.boardredis.service.dto.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,

) {
    @Transactional
    fun createComment(postId: Long, createRequestDto: CommentCreateRequestDto): Long {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        return commentRepository.save(createRequestDto.toEntity(post)).id
    }

}
