package com.example.boardredis.service

import com.example.boardredis.domain.Post
import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.exception.PostNotUpdateableException
import com.example.boardredis.repository.PostRepository
import com.example.boardredis.service.dto.PostCreateRequestDto
import com.example.boardredis.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
) : BehaviorSpec({
    given("게시글 생성시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(PostCreateRequestDto(
                title = "제목",
                content = "내용",
                createdBy = "harris",
            ))
            then("게시글이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "harris"
            }
        }
    }
    given("게시글 수정시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "harris"))
        When("정상 수정시") {
            val updatedId = postService.updatePost(saved.id, PostUpdateRequestDto(
                title = "제목바뀜",
                content = "내용바뀜",
                updatedBy = "harris"
            ))
            then("게시글이 정상적으로 수정됨을 확인한다."){
                saved.id shouldBe updatedId
                val updated = postRepository.findByIdOrNull(updatedId)
                updated shouldNotBe null
                updated?.title shouldBe "제목바뀜"
                updated?.content shouldBe "내용바뀜"
//                updated?.updatedBy shouldBe "calvin"
            }
        }
        When("게시글이 없을때") {
            then("게시글을 찾을 수 없다라는 예외가 발생") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(9999L, PostUpdateRequestDto(
                        title = "제목바뀜",
                        content = "내용바뀜",
                        updatedBy = "calvin"
                    ))
                }
            }
        }
        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다 예외가 발생") {
                shouldThrow<PostNotUpdateableException> {
                    postService.updatePost(1L, PostUpdateRequestDto(
                        title = "제목바뀜",
                        content = "내용바뀜",
                        updatedBy = "calvin"
                    ))
                }
            }
        }
    }
})
