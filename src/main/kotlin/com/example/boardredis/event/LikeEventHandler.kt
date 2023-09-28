package com.example.boardredis.event

import com.example.boardredis.domain.Like
import com.example.boardredis.event.dto.LikeEvent
import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.repository.LikeRepository
import com.example.boardredis.repository.PostRepository
import com.example.boardredis.util.RedisUtil
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class LikeEventHandler(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val redisUtil: RedisUtil,
) {
    @Async
    @TransactionalEventListener(LikeEvent::class)
    fun handle(event: LikeEvent)  {
        Thread.sleep(3000)
        val post = postRepository.findByIdOrNull(event.postId) ?: throw PostNotFoundException()
        redisUtil.increment(redisUtil.getLikeCountKey(event.postId))
        likeRepository.save(Like(post, event.createdBy)).id
    }
}
