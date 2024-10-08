package com.demo.redisson.lock.service

import com.demo.redisson.annotation.RedissonLocked
import com.demo.redisson.service.StockService
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StockServiceForLock(
    val redissonClient: RedissonClient,
) : StockService() {

    @RedissonLocked("#key")
    override fun decrease(
        key: String,
        count: Int
    ) {
        decreaseCommon(key, count)
    }

    override fun setStockValue(
        key: String,
        value: Int
    ) = redissonClient.getBucket<String>(key).set(value.toString(), TTL, TTL_TIMEUNIT)

    override fun getStockValue(
        key: String,
    ): Int = redissonClient.getBucket<String>(key).get().toInt()

    override fun getThreadName(): String = Thread.currentThread().name
}