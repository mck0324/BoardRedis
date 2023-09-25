package com.example.boardredis.repository

import com.example.boardredis.domain.Post
import com.example.boardredis.domain.QPost.post
import com.example.boardredis.service.dto.PostSearchRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

interface PostRepository : JpaRepository<Post, Long>, CustomPostRepository

interface CustomPostRepository {
    fun findPageBy(pageRequest: Pageable, postSearchRequestDto: PostSearchRequestDto): Page<Post>
}

class CustomPostRepositoryImpl : CustomPostRepository, QuerydslRepositorySupport(Post::class.java) {
    override fun findPageBy(pageRequest: Pageable, postSearchRequestDto: PostSearchRequestDto): Page<Post> {
        val result = from(post)
            .where(
                postSearchRequestDto.title?. let { post.title.contains(it) },
                postSearchRequestDto.createdBy?. let { post.createdBy.eq(it) }
//                postSearchRequestDto.tag?. let { post.tags.any().name.eq(it) }
            )
            .orderBy(post.createdAt.desc())
            .offset(pageRequest.offset)
            .limit(pageRequest.pageSize.toLong())
            .fetchResults()
        return PageImpl(result.results, pageRequest, result.total)
    }
}
