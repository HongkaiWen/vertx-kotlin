package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import java.lang.Exception


/**
 * 通过promise方式来实现异步编排
 * a + (（b -c）+ d) -e -f + g
 * 1. 通过vertx.core.promise实现promise模式
 * 2. 通过promise.compose实现链式调用
 * 3. 通过中缀函数实现一点骚气的操作
 *
 */
class PromiseLineVerticle3 : AbstractVerticle(){


    lateinit var webClient: WebClient

    override fun start(startPromise: Promise<Void>?) {
        webClient = WebClient.create(vertx)

        var eventBus = vertx.eventBus()
        eventBus.consumer<JsonObject>("calc.promise.line3"){ msg ->
            var msgBody = msg.body()
            var a = msgBody.getInteger("a", 0)
            var b = msgBody.getInteger("b", 0)
            var c = msgBody.getInteger("c", 0)
            var d = msgBody.getInteger("d", 0)
            var e = msgBody.getInteger("e", 0)
            var f = msgBody.getInteger("f", 0)
            var g = msgBody.getInteger("g", 0)

            //只要一个onFailure即可
            (b asyncSub c)
                .compose { it asyncAdd d }
                .compose { it asyncAdd a }
                .compose { it asyncSub e }
                .compose { it asyncSub f }
                .compose { it asyncAdd g }
                .onSuccess { msg.reply(it.toString()) }
                .onFailure{msg.fail(500, it.message)}
        }
    }



    infix fun Int.asyncAdd(input : Int) : Future<Int> {
        return calc(this, input, CalcOperator.add)
    }

    infix fun Int.asyncSub(input : Int) : Future<Int> {
        return calc(this, input, CalcOperator.sub)
    }

    /**
     * 所有异常必须被处理
     */
    fun calc(a: Int, b: Int, operator: CalcOperator) : Future<Int> {
        var promise = Promise.promise<Int>()

        webClient.get(7777, "pi", "/${operator.name}?a=$a&b=$b").send{
            if (it.succeeded()) {
                try{
                    var addResult = it.result().bodyAsString().toInt()
                    promise.complete(addResult)
                    println("$a - $b = $addResult")
                } catch (e: Exception) {
                    promise.fail(e)
                }
            } else {
                it.cause().printStackTrace()
                promise.fail("calc failed $a add $b")
            }
        }

        return promise.future()
    }
}

enum class CalcOperator {
    add, sub
}

