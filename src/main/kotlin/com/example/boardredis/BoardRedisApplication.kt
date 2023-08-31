package com.example.boardredis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BoardRedisApplication

fun main(args: Array<String>) {
    runApplication<BoardRedisApplication>(*args)
}
