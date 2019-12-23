package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient


/**
 * 通过promise方式来实现异步编排
 * a + (（b -c）+ d) -e -f + g
 *
 * 如果只是实现一级的调用，promise 比 callback，略有优势：
 * 类似这样：sub(b, c).future().onSuccess { s1 -> add(s1, d) }.onFailure(failerHandler)
 * 如果是多级的，vertx 的 promise是不支持的
 * 如果是多级的，就是reactive要解决的问题了吧
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


            var failerHandler : (Throwable) -> Unit = {msg.fail(500, it.message)}

            (b asyncSub c)
                .compose { it asyncAdd d }
                .compose { it asyncAdd a }
                .compose { it asyncSub e }
                .compose { it asyncSub f }
                .compose { it asyncAdd g }
                .onSuccess { msg.reply(it.toString()) }
                .onFailure(failerHandler)
        }
    }

    infix fun Int.asyncAdd(input : Int) : Future<Int> {
        return calc(this, input, CalcOperator.add)
    }

    infix fun Int.asyncSub(input : Int) : Future<Int> {
        return calc(this, input, CalcOperator.sub)
    }

    fun calc(a: Int, b: Int, operator: CalcOperator) : Future<Int> {
        var promise = Promise.promise<Int>()

        webClient.get(7777, "pi", "/${operator.name}?a=$a&b=$b").send{
            if (it.succeeded()) {
                var addResult = it.result().bodyAsString().toInt()
                promise.complete(addResult)
                println("$a - $b = $addResult")
            } else {
                promise.fail("calc failed $a add $it")
            }
        }

        return promise.future()
    }
}

enum class CalcOperator {
    add, sub
}

