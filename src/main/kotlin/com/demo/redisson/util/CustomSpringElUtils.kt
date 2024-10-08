package com.demo.redisson.util

import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

class CustomSpringElUtils {
    companion object {
        fun getDynamicValue(
            parameterNames: Array<String>,
            args: Array<Any>,
            key: String,
        ): Any? {
            val parser: ExpressionParser = SpelExpressionParser()
            val context = StandardEvaluationContext()

            parameterNames.forEachIndexed { index, name ->
                context.setVariable(name, args[index])
            }

            return parser.parseExpression(key).getValue(context, Any::class.java)
        }
    }
}