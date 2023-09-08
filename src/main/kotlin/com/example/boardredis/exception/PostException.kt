package com.example.boardredis.exception

open class PostException( message: String ) : RuntimeException(message)

class PostNotFoundException(): PostException("게시글을 찾을 수 없습니다.")

class PostNotUpdateableException(): PostException("게시글을 수정할 수 없습니다.")

class PostNotDeleteableException(): PostException("게시글을 삭제할 수 없습니다.")
