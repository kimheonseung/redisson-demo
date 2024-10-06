package com.demo.redisson.lock.service

import com.demo.redisson.service.StockService
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StockServiceForLock(
    val redissonClient: RedissonClient,
) : StockService() {

    override fun decrease(count: Int) {
        val lockName = "$KEY:for-lock"
        val redissonLock: RLock = redissonClient.getLock(lockName)

        try {
            if (!redissonLock.tryLock(1, 3, TimeUnit.SECONDS)) {
                return
            }
            decreaseCommon(count)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (redissonLock.isLocked) {
                redissonLock.unlock()
            }
        }
    }

    override fun setStockValue(
        value: Int
    ) = redissonClient.getBucket<String>(KEY).set(value.toString(), TTL, TTL_TIMEUNIT)

    override fun getStockValue(): Int = redissonClient.getBucket<String>(KEY).get().toInt()

    override fun getThreadName(): String = Thread.currentThread().name
}