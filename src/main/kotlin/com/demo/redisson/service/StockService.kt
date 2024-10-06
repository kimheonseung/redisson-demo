package com.demo.redisson.service

import java.util.concurrent.TimeUnit

abstract class StockService {
    val KEY: String
        get() = "STOCK"
    val TTL: Long
        get() = 10
    val TTL_TIMEUNIT: TimeUnit
        get() = TimeUnit.SECONDS

    abstract fun getThreadName(): String
    abstract fun decrease(count: Int)
    abstract fun setStockValue(value: Int)
    abstract fun getStockValue(): Int

    fun decreaseCommon(count: Int) {
        val threadName = getThreadName()
        val currentStock = getStockValue()
        println("[${threadName}] 남은 재고: $currentStock")

        if (currentStock <= 0) {
            println("[${threadName}] 남은 재고가 없음: $currentStock")
            return
        }
        println("[${threadName}] 진행중.. / 현재 재고: $currentStock")
        setStockValue(currentStock - count)
    }
}