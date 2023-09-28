package com.example.boardredis.service

import com.example.boardredis.domain.Post
import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.repository.LikeRepository
import com.example.boardredis.repository.PostRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.testcontainers.containers.GenericContainer

@SpringBootTest
class LikeServiceTest(
    private val likeService: LikeService,
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
) : BehaviorSpec({
    val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
    beforeSpec {
        redisContainer.portBindings.add("16379:6379")
        redisContainer.start()
        listener(redisContainer.perSpec())
    }
    afterSpec {
        redisContainer.stop()
    }
    given("좋아요 생성시") {
        val saved = postRepository.save(Post("harris", "title", "content"))
        When("인풋이 정상적으로 돌아오면") {
            val likeId = likeService.createLike(saved.id, "harris")
            then("좋아요가 정상적으로 생성됨") {
                val like = likeRepository.findByIdOrNull(likeId)
                like shouldNotBe null
                like?.createdBy shouldBe "harris"
            }
        }
        When("게시글이 존재하지 않으면") {
            then("존재하지 않는 게시글 예외가 발생") {
                shouldThrow<PostNotFoundException> { likeService.createLike(9999L, "harris") }
            }
        }
    }
})
