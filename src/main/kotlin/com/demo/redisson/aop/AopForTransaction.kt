package com.demo.redisson.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class AopForTransaction {
    @Transactional(propagation = Propagation.REQUIRED)
    fun proceed(joinPoint: ProceedingJoinPoint) = joinPoint.proceed()
}