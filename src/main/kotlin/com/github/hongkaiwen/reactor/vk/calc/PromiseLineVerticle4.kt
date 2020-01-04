package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import java.util.concurrent.CompletableFuture


/**
 * 通过promise方式来实现异步编排
 * a + (（b -c）+ d) -e -f + g
 * 1. 通过CompletableFuture 实现 promise 模式
 * 2. 通过promise.thenCompose 实现链式调用
 * 3. 通过中缀函数实现一点骚气的操作
 *
 */
class PromiseLineVerticle4 : AbstractVerticle(){


    lateinit var webClient: WebClient

    override fun start(startPromise: Promise<Void>?) {
        webClient = WebClient.create(vertx)

        var eventBus = vertx.eventBus()
        eventBus.consumer<JsonObject>("calc.promise.line4"){ msg ->
            var msgBody = msg.body()
            var a = msgBody.getInteger("a", 0)
            var b = msgBody.getInteger("b", 0)
            var c = msgBody.getInteger("c", 0)
            var d = msgBody.getInteger("d", 0)
            var e = msgBody.getInteger("e", 0)
            var f = msgBody.getInteger("f", 0)
            var g = msgBody.getInteger("g", 0)


            (b asyncSub c).thenCompose {
                    var promise = CompletableFuture<Int>()
                    if(it > 5){
                        promise.complete(it)
                    } else {
                        promise.complete(6)
                    }
                    promise
                }
                .thenCompose { it asyncAdd d }
                .thenCompose { it asyncAdd a }
                .thenCompose { it asyncSub e }
                .thenCompose { it asyncSub f }
                .thenCompose { it asyncAdd g }
                .thenAccept { msg.reply(it.toString()) }
                .exceptionally {
                    msg.fail(500, it.message)
                    null
                }
        }
    }



    infix fun Int.asyncAdd(input : Int) : CompletableFuture<Int> {
        return calc(this, input, CalcOperator.add)
    }

    infix fun Int.asyncSub(input : Int) : CompletableFuture<Int> {
        return calc(this, input, CalcOperator.sub)
    }

    /**
     * 所有异常必须被处理
     */
    fun calc(a: Int, b: Int, operator: CalcOperator) : CompletableFuture<Int> {
        var promise = CompletableFuture<Int>()

        webClient.get(7777, "pi", "/${operator.name}?a=$a&b=$b")
            .expect(ResponsePredicate.SC_OK).send{
            if (it.succeeded()) {
                try{
                    var addResult = it.result().bodyAsString().toInt()
                    promise.complete(addResult)
                    println("$a - $b = $addResult")
                } catch (e: Exception) {
                    promise.completeExceptionally(e)
                }
            } else {
                it.cause().printStackTrace()
                promise.completeExceptionally(RuntimeException("calc failed $a add $b"))
            }
        }

        return promise
    }
}
