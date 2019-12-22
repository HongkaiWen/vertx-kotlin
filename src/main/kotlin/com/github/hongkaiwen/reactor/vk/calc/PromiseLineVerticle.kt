package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
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
class PromiseLineVerticle : AbstractVerticle(){


    lateinit var webClient: WebClient

    override fun start(startPromise: Promise<Void>?) {
        webClient = WebClient.create(vertx)

        var eventBus = vertx.eventBus()
        eventBus.consumer<JsonObject>("calc.promise.line"){ msg ->
            var msgBody = msg.body()
            var a = msgBody.getInteger("a", 0)
            var b = msgBody.getInteger("b", 0)
            var c = msgBody.getInteger("c", 0)
            var d = msgBody.getInteger("d", 0)
            var e = msgBody.getInteger("e", 0)
            var f = msgBody.getInteger("f", 0)
            var g = msgBody.getInteger("g", 0)


            var failerHandler : (Throwable) -> Unit = {msg.fail(500, it.message)}

            //ctrl + shift + p show type
            sub(b, Promise.promise<Int>().just(c)).let { add(d, it) }.let { add(a, it) }.let { sub(it, e) }
                .let { sub(it, f) }.let { add(g, it) }.future().onSuccess { msg.reply(it.toString()) }.onFailure(failerHandler)


            sub(b, Promise.promise<Int>().just(c)).let { add(d, it) }.let { add(a, it) }.let { sub(it, e) }
                .let { sub(it, f) }.let { add(g, it) }.future().onSuccess { msg.reply(it.toString()) }.onFailure(failerHandler)
        }
    }


    fun add(a: Int, inPromise: Promise<Int>) : Promise<Int>{
        var promise = Promise.promise<Int>()

        inPromise.future().onSuccess {
            webClient.get(7777, "pi", "/add?a=$a&b=$it").send{
                if (it.succeeded()) {
                    var addResult = it.result().bodyAsString().toInt()
                    promise.complete(addResult)
                    println("$a + $it = $addResult")
                } else {
                    promise.fail("calc failed $a add $it")
                }
            }
        }.onFailure {
            promise.fail(it)
        }

        return promise
    }

    fun sub(a: Int, inPromise: Promise<Int>) : Promise<Int>{
        var promise = Promise.promise<Int>()

        inPromise.future().onSuccess {
            webClient.get(7777, "pi", "/sub?a=$a&b=$it").send{
                if (it.succeeded()) {
                    var addResult = it.result().bodyAsString().toInt()
                    promise.complete(addResult)
                    println("$a + $it = $addResult")
                } else {
                    promise.fail("calc failed $a add $it")
                }
            }
        }.onFailure {
            promise.fail(it)
        }

        return promise
    }

    fun sub(inPromise: Promise<Int>, a : Int) : Promise<Int>{
        var promise = Promise.promise<Int>()

        inPromise.future().onSuccess {
            webClient.get(7777, "pi", "/sub?a=$it&b=$a").send{
                if (it.succeeded()) {
                    var addResult = it.result().bodyAsString().toInt()
                    promise.complete(addResult)
                    println("$it + $a = $addResult")
                } else {
                    promise.fail("calc failed $it add $a")
                }
            }
        }.onFailure {
            promise.fail(it)
        }

        return promise
    }

}

fun <T> Promise<T>.just(t: T):Promise<T> {
    var promise = Promise.promise<T>()
    promise.complete(t)
    return promise
}