package com.example.boardredis.service

import com.example.boardredis.exception.PostNotDeleteableException
import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.repository.PostRepository
import com.example.boardredis.service.dto.PostCreateRequestDto
import com.example.boardredis.service.dto.PostUpdateRequestDto
import com.example.boardredis.service.dto.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
) {
    @Transactional
    fun createPost(requestDto: PostCreateRequestDto): Long {
        return postRepository.save(requestDto.toEntity()).id
    }

    @Transactional
    fun updatePost(id: Long, requestDto: PostUpdateRequestDto): Long {
        val post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        post.update(requestDto)
        return id
    }

    @Transactional
    fun deletePost(id: Long, deletedBy: String): Long {
        val post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        if (post.createdBy != deletedBy) throw PostNotDeleteableException()
        postRepository.delete(post)
        return id
    }
}
