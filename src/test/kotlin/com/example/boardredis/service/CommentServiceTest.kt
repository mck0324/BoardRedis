package com.example.boardredis.service

import com.example.boardredis.repository.CommentRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class CommentServiceTest(
    private val commentService: CommentService,
    private val commentRepository: CommentRepository,
): BehaviorSpec ({
    given("댓글 생성시") {
        When("인풋이 정상적으로 들어오면") {
            val commentId = commentService.createComment(CommentCreateReqeustDto(
                postId = 1L,
                content = "댓글입니다.",
                createdBy = "댓글 생성자",
            ))
            then("정상 생성됨을 확인") {
                commentId shouldBeGreaterThan 0L
                val comment = commentRepository.findByIdOrNull(commentId)
                comment shouldNotBe null

            }
        }
    }
})
