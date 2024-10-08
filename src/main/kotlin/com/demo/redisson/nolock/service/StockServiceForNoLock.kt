package com.demo.redisson.nolock.service

import com.demo.redisson.service.StockService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class StockServiceForNoLock(
    val redisTemplate: RedisTemplate<String, String>,
) : StockService() {

    override fun decrease(
        key: String,
        count: Int,
    ) {
        decreaseCommon(key, count)
    }

    override fun setStockValue(
        key: String,
        value: Int,
    ) = redisTemplate.opsForValue().set(key, value.toString(), TTL, TTL_TIMEUNIT)

    override fun getStockValue(
        key: String,
    ) = redisTemplate.opsForValue().get(key)!!.toInt()

    override fun getThreadName(): String = Thread.currentThread().name

}
