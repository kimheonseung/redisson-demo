package com.demo.redisson

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedissonDemoApplication

fun main(args: Array<String>) {
    runApplication<RedissonDemoApplication>(*args)
}
