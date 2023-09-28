package com.example.boardredis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class BoardRedisApplication

fun main(args: Array<String>) {
    runApplication<BoardRedisApplication>(*args)
}
