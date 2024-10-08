package com.demo.redisson.lock

import com.demo.redisson.lock.service.StockServiceForLock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.test.assertEquals

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StockServiceForLockTests(
    @Autowired
    val stockServiceForLock: StockServiceForLock,
) {
    val totalAmount: Int = 100
    val key = "STOCK"

    inner class BuyerForLock(
        private val count: Int,
        private val countDownLatch: CountDownLatch,
    ) : Runnable {
        override fun run() {
            stockServiceForLock.decrease(key, this.count)
            this.countDownLatch.countDown()
        }
    }

    @BeforeEach
    fun resetStock() {
        this.stockServiceForLock.setStockValue(key, this.totalAmount)
    }

    @Test
    @Order(1)
    fun assertAmount() {
        val redisAmount = stockServiceForLock.getStockValue(key)

        assertEquals(this.totalAmount, redisAmount)
    }

    @Test
    @Order(2)
    fun assertDecrease() {
        val decreaseCount = 2

        this.stockServiceForLock.decrease(key, decreaseCount)

        val decreasedAmount = this.stockServiceForLock.getStockValue(key)
        assertEquals(this.totalAmount - decreaseCount, decreasedAmount)
    }

    @Test
    @Order(3)
    fun assertLock() {
        val buyerCount = 100
        val buyCount = 2
        val countDownLatch = CountDownLatch(buyerCount)

        Stream
            .generate {
                Thread(BuyerForLock(buyCount, countDownLatch))
            }
            .limit(buyerCount.toLong())
            .collect(Collectors.toList())
            .forEach {
                it.start()
            }

        countDownLatch.await()

        val currentStockValue = this.stockServiceForLock.getStockValue(key)
        assertEquals(0, currentStockValue)
    }
}
