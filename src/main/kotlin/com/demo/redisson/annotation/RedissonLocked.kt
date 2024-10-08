package com.demo.redisson.annotation

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RedissonLocked(
    /**
     * 락 이름
     */
    val key: String,

    /**
     * 락 시간 단위
     */
    val timeUnit: TimeUnit = TimeUnit.SECONDS,

    /**
     * 락 획득 대기 시간
     */
    val waitTime: Long = 5L,

    /**
     * 락 보유 시간
     */
    val leaseTime: Long = 3L
)