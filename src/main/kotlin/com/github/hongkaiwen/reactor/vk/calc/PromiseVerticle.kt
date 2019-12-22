package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient


/**
 * 通过promise方式来实现异步编排
 * a + (（b -c）+ d) -e -f + g
 */
class PromiseVerticle : AbstractVerticle(){


    lateinit var webClient: WebClient

    override fun start(startPromise: Promise<Void>?) {
        webClient = WebClient.create(vertx)

        var eventBus = vertx.eventBus()
        eventBus.consumer<JsonObject>("calc.promise"){ msg ->
            var msgBody = msg.body()
            var a = msgBody.getInteger("a", 0)
            var b = msgBody.getInteger("b", 0)
            var c = msgBody.getInteger("c", 0)
            var d = msgBody.getInteger("d", 0)
            var e = msgBody.getInteger("e", 0)
            var f = msgBody.getInteger("f", 0)
            var g = msgBody.getInteger("g", 0)

            sub(b, c).future().onSuccess {
                add(it, d).future().onSuccess {
                    add(a, it).future().onSuccess {
                        sub(it, e).future().onSuccess {
                            sub(it, f).future().onSuccess {
                                add(it, g).future().onSuccess {
                                    msg.reply(it.toString())
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    fun add(a: Int, b: Int) : Promise<Int> {
        var promise = Promise.promise<Int>()

        webClient.get(7777, "pi", "/add?a=$a&b=$b").send{
            if (it.succeeded()) {
                var addResult = it.result().bodyAsString().toInt()
                promise.complete(addResult)
            } else {
                promise.fail("calc failed $a add $b")
            }
        }

        return promise
    }

    fun sub(a: Int, b: Int) : Promise<Int> {
        var promise = Promise.promise<Int>()

        webClient.get(7777, "pi", "/sub?a=$a&b=$b").send{
            if (it.succeeded()) {
                var addResult = it.result().bodyAsString().toInt()
                promise.complete(addResult)
            } else {
                promise.fail("calc failed $a sub $b")
            }
        }

        return promise
    }


}