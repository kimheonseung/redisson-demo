package com.demo.redisson.aop

import com.demo.redisson.annotation.RedissonLocked
import com.demo.redisson.util.CustomSpringElUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Aspect
@Component
class RedissonLockAop(
    val redissonClient: RedissonClient,
    val aopForTransaction: AopForTransaction,
) {
    private val redissonLockKeyPrefix = "RLOCK:"
    private val log = KotlinLogging.logger { }

    @Around("@annotation(com.demo.redisson.annotation.RedissonLocked)")
    fun lock(
        joinPoint: ProceedingJoinPoint
    ): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val redissonLocked = method.getAnnotation(RedissonLocked::class.java)

        val key = redissonLockKeyPrefix + CustomSpringElUtils.getDynamicValue(methodSignature.parameterNames, joinPoint.args, redissonLocked.key)
        val rLock = redissonClient.getLock(key)
        log.info("Try to require Redisson Lock for key {}", key)

        try {
            if (!rLock.tryLock(redissonLocked.waitTime, redissonLocked.leaseTime, redissonLocked.timeUnit)) {
                log.warn("Redisson Lock is not available for key {}", key)
                return null
            }

            log.info("Success to require Redisson Lock for key {}", key)
            return aopForTransaction.proceed(joinPoint)
        } catch (e: Exception) {
            log.error(e.message, e)
            throw e
        } finally {
            try {
                rLock.unlock()
            } catch (e: Exception) {
                log.info("Redisson Lock Already UnLock {} {}", method.name, key)
            }
        }
    }
}