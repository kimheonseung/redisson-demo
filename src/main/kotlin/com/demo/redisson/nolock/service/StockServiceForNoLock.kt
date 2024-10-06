package com.demo.redisson.nolock.service

import com.demo.redisson.service.StockService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class StockServiceForNoLock(
    val redisTemplate: RedisTemplate<String, String>,
) : StockService() {

    override fun decrease(
        count: Int,
    ) {
        decreaseCommon(count)
    }

    override fun setStockValue(
        value: Int,
    ) = redisTemplate.opsForValue().set(KEY, value.toString(), TTL, TTL_TIMEUNIT)

    override fun getStockValue() = redisTemplate.opsForValue().get(KEY)!!.toInt()

    override fun getThreadName(): String = Thread.currentThread().name

}
