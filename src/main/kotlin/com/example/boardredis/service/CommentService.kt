package com.example.boardredis.service

import com.example.boardredis.exception.CommentDeletableException
import com.example.boardredis.exception.CommentNotFoundException
import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.repository.CommentRepository
import com.example.boardredis.repository.PostRepository
import com.example.boardredis.service.dto.CommentCreateRequestDto
import com.example.boardredis.service.dto.CommentUpdateRequestDto
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

    @Transactional
    fun updateComment(id: Long, updateRequestDto: CommentUpdateRequestDto): Long {
        val comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        comment.update(updateRequestDto)
        return comment.id
    }

    @Transactional
    fun deleteComment(id: Long, deletedBy: String): Long {
        val comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        if (comment.createdBy != deletedBy) {
            throw CommentDeletableException()
        }
        commentRepository.delete(comment)
        return id
    }
}
