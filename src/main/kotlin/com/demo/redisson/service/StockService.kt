package com.demo.redisson.service

import java.util.concurrent.TimeUnit

abstract class StockService {
    val TTL: Long
        get() = 10
    val TTL_TIMEUNIT: TimeUnit
        get() = TimeUnit.SECONDS

    abstract fun getThreadName(): String
    abstract fun decrease(key: String, count: Int)
    abstract fun setStockValue(key: String, value: Int)
    abstract fun getStockValue(key: String): Int

    fun decreaseCommon(key: String, count: Int) {
        val threadName = getThreadName()
        val currentStock = getStockValue(key)
        println("[${threadName}] 남은 재고: $currentStock")

        if (currentStock <= 0) {
            println("[${threadName}] 남은 재고가 없음: $currentStock")
            return
        }
        println("[${threadName}] 진행중.. / 현재 재고: $currentStock")
        setStockValue(key, currentStock - count)
    }
}