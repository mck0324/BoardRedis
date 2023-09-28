package com.example.boardredis.util

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisUtil (
    private val redisTemplate: RedisTemplate<String, Any>,
){
    fun setData(key: String, value: Any) {
        return redisTemplate.opsForValue().set(key,value.toString())
    }

    fun getData(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }
}
