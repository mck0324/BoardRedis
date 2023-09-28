package com.example.boardredis.service

import com.example.boardredis.domain.Comment
import com.example.boardredis.domain.Post
import com.example.boardredis.domain.Tag
import com.example.boardredis.exception.PostNotDeleteableException
import com.example.boardredis.exception.PostNotFoundException
import com.example.boardredis.exception.PostNotUpdateableException
import com.example.boardredis.repository.CommentRepository
import com.example.boardredis.repository.PostRepository
import com.example.boardredis.repository.TagRepository
import com.example.boardredis.service.dto.PostCreateRequestDto
import com.example.boardredis.service.dto.PostSearchRequestDto
import com.example.boardredis.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.testcontainers.containers.GenericContainer

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tagRepository: TagRepository,
    private val likeService: LikeService,
) : BehaviorSpec({
    val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
    afterSpec {
        redisContainer.stop()
    }
    beforeSpec {
        redisContainer.portBindings.add("16379:6379")
        redisContainer.start()
        listener(redisContainer.perSpec())
        postRepository.saveAll(
            listOf(
                Post(title = "title1", content = "content", createdBy = "harris1", tags = listOf("tag1", "tag2")),
                Post(title = "title12", content = "content", createdBy = "harris1", tags = listOf("tag1", "tag2")),
                Post(title = "title13", content = "content", createdBy = "harris1", tags = listOf("tag1", "tag2")),
                Post(title = "title14", content = "content", createdBy = "harris1", tags = listOf("tag1", "tag2")),
                Post(title = "title15", content = "content", createdBy = "harris1", tags = listOf("tag1", "tag2")),
                Post(title = "title6", content = "content", createdBy = "harris2", tags = listOf("tag1", "tag5")),
                Post(title = "title7", content = "content", createdBy = "harris2", tags = listOf("tag1", "tag5")),
                Post(title = "title8", content = "content", createdBy = "harris2", tags = listOf("tag1", "tag5")),
                Post(title = "title9", content = "content", createdBy = "harris2", tags = listOf("tag1", "tag5")),
                Post(title = "title10", content = "content", createdBy = "harris2", tags = listOf("tag1", "tag5"))
            )
        )
    }
    given("게시글 생성시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "harris"
                )
            )
            then("게시글이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "harris"
            }
        }
        When("태그가 추가되면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "harris",
                    tags = listOf("tag1", "tag2")
                )
            )
            then("태그가 정상적으로 추가됨을 확인") {
                val tags = tagRepository.findByPostId(postId)
                tags.size shouldBe 2
                tags[0].name shouldBe "tag1"
                tags[1].name shouldBe "tag2"
            }
        }
    }
    given("게시글 수정시") {
        val saved = postRepository.save(
            Post(title = "title", content = "content", createdBy = "harris", tags = listOf("tag1", "tag2"))
        )
        When("정상 수정시") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "제목바뀜",
                    content = "내용바뀜",
                    updatedBy = "harris"
                )
            )
            then("게시글이 정상적으로 수정됨을 확인한다.") {
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
                    postService.updatePost(
                        9999L,
                        PostUpdateRequestDto(
                            title = "제목바뀜",
                            content = "내용바뀜",
                            updatedBy = "calvin"
                        )
                    )
                }
            }
        }
        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다 예외가 발생") {
                shouldThrow<PostNotUpdateableException> {
                    postService.updatePost(
                        1L,
                        PostUpdateRequestDto(
                            title = "제목바뀜",
                            content = "내용바뀜",
                            updatedBy = "calvin"
                        )
                    )
                }
            }
        }
        When("태그가 수정되었을 때") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "update title",
                    content = "update content",
                    updatedBy = "harris",
                    tags = listOf("tag1", "tag2", "tag3")
                )
            )
            then("정상적으로 수정됨을 확인") {
                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag3"
            }
            then("태그 순서가 변경되었을때 정상적으로 변경됨을 확인") {
                postService.updatePost(
                    saved.id,
                    PostUpdateRequestDto(
                        title = "update title",
                        content = "update content",
                        updatedBy = "harris",
                        tags = listOf("tags3", "tags2", "tag1")
                    )
                )
                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag1"
            }
        }
    }

    given("게시글 삭제시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "harris"))
        When("정상 삭제시") {
            val postId = postService.deletePost(saved.id, "harris")
            then("게시글 정상적으로 삭제됨을 확인") {
                postId shouldBe saved.id
                postRepository.findByIdOrNull(postId) shouldBe null
            }
        }
        When("작성자가 동일하지 않으면") {
            val saved2 = postRepository.save(Post(title = "title", content = "content", createdBy = "harris"))
            then("삭제할 수 없는 게시물 입니다. 예외가 발생") {
                shouldThrow<PostNotDeleteableException> { postService.deletePost(saved2.id, "chris") }
            }
        }
    }

    given("게시글 상세조회시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "harris"))
        tagRepository.saveAll(
            listOf(
                Tag(name = "tag1", post = saved, createdBy = "harris"),
                Tag(name = "tag2", post = saved, createdBy = "harris"),
                Tag(name = "tag3", post = saved, createdBy = "harris")
            )
        )
        likeService.createLike(saved.id, "harris")
        likeService.createLike(saved.id, "harris1")
        likeService.createLike(saved.id, "harris2")
        When("정상 조회시") {
            val post = postService.getPost(saved.id)
            then("게시글의 내용이 정상적으로 반환됨을 확인") {
                post.id shouldBe saved.id
                post.title shouldBe "title"
                post.content shouldBe "content"
                post.createdBy shouldBe "harris"
            }
            then("태그가 정상적으로 조회됨을 확인") {
                post.tags.size shouldBe 3
                post.tags[0] shouldBe "tag1"
                post.tags[1] shouldBe "tag2"
                post.tags[2] shouldBe "tag3"
            }
            then("좋아요 개수가 조회됨을 확인") {
                post.likeCount shouldBe 3
            }
        }
        When("게시글이 없을때") {
            then("게시글을 찾을 수 없다라는 예외가 발생") {
                shouldThrow<PostNotFoundException> { postService.getPost(9999L) }
            }
        }
        When("댓글 추가시") {
            commentRepository.save(
                Comment(
                    content = "댓글 내용",
                    post = saved,
                    createdBy = "댓글 작성자"
                )
            )
            commentRepository.save(
                Comment(
                    content = "댓글 내용1",
                    post = saved,
                    createdBy = "댓글 작성자"
                )
            )
            commentRepository.save(
                Comment(
                    content = "댓글 내용2",
                    post = saved,
                    createdBy = "댓글 작성자"
                )
            )
            val post = postService.getPost(saved.id)
            then("댓글이 함께 조회됨을 확인") {
                post.comments.size shouldBe 3
                post.comments[0].content shouldBe "댓글 내용"
                post.comments[1].content shouldBe "댓글 내용1"
                post.comments[2].content shouldBe "댓글 내용2"
                post.comments[0].createdBy shouldBe "댓글 작성자"
                post.comments[1].createdBy shouldBe "댓글 작성자"
                post.comments[2].createdBy shouldBe "댓글 작성자"
            }
        }
    }

    given("게시글 목록조회시") {
        When("정상 조회시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글 페이지 반환됨을 확인") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title"
                postPage.content[0].createdBy shouldContain "harris"
            }
        }
        When("title 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "title1"))
            then("title에 해당하는 게시글이 반환됨") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title1"
                postPage.content[0].createdBy shouldContain "harris"
            }
        }
        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "harris1"))
            then("작성자에 해당하는 게시글이 반환됨") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title1"
                postPage.content[0].createdBy shouldBe "harris1"
            }
            then("첫번째 태그가 함께 조회됨을 확인") {
                postPage.content.forEach {
                    it.firstTag shouldBe "tag1"
                }
            }
        }
        When("태그로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            then("태그에 해당하는 게시글이 반환") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldBe "title10"
                postPage.content[1].title shouldBe "title9"
                postPage.content[2].title shouldBe "title8"
                postPage.content[3].title shouldBe "title7"
                postPage.content[4].title shouldBe "title6"
            }
        }
        When("좋아요가 2개 추가되었을 때") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            postPage.content.forEach {
                likeService.createLike(it.id, "harris1")
                likeService.createLike(it.id, "harris2")
            }
            val likedPostPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            then("좋아요 개수가 정상적으로 조회됨") {
                likedPostPage.content.forEach {
                    it.likeCount shouldBe 2
                }
            }
        }
    }
})
