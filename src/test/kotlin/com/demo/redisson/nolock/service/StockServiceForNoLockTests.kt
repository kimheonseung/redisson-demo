package com.demo.redisson.nolock.service

import org.junit.jupiter.api.Assertions.assertNotEquals
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
class StockServiceForNoLockTests(
    @Autowired
    val stockServiceForNoLock: StockServiceForNoLock,
) {
    val totalAmount: Int = 100

    inner class BuyerForNoLock(
        private val count: Int,
        private val countDownLatch: CountDownLatch,
    ) : Runnable {
        override fun run() {
            stockServiceForNoLock.decrease(this.count)
            this.countDownLatch.countDown()
        }
    }

    @BeforeEach
    fun resetStock() {
        this.stockServiceForNoLock.setStockValue(this.totalAmount)
    }

    @Test
    @Order(1)
    fun assertAmount() {
        val redisAmount = stockServiceForNoLock.getStockValue()

        assertEquals(this.totalAmount, redisAmount)
    }

    @Test
    @Order(2)
    fun assertDecrease() {
        val decreaseCount = 2

        this.stockServiceForNoLock.decrease(decreaseCount)

        val decreasedAmount = this.stockServiceForNoLock.getStockValue()
        assertEquals(this.totalAmount - decreaseCount, decreasedAmount)
    }

    @Test
    @Order(3)
    fun assertNoLock() {
        val buyerCount = 100
        val buyCount = 2
        val countDownLatch = CountDownLatch(buyerCount)

        Stream
            .generate {
                Thread(BuyerForNoLock(buyCount, countDownLatch))
            }
            .limit(buyerCount.toLong())
            .collect(Collectors.toList())
            .forEach {
                it.start()
            }

        countDownLatch.await()

        val currentStockValue = this.stockServiceForNoLock.getStockValue()
        assertNotEquals(0, currentStockValue)
    }
}