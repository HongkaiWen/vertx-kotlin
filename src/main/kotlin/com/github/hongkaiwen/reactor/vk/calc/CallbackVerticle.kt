package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient

/**
 * 通过回调方式来实现异步编排
 * a + (（b -c）+ d) -e -f + g
 */
class CallbackVerticle : AbstractVerticle(){

    lateinit var webClient: WebClient

    override fun start(startPromise: Promise<Void>?) {
        webClient = WebClient.create(vertx)

        var eventBus = vertx.eventBus()
        eventBus.consumer<JsonObject>("calc.callback"){
            var msgBody = it.body()
            var a = msgBody.getInteger("a", 0)
            var b = msgBody.getInteger("b", 0)
            var c = msgBody.getInteger("c", 0)
            var d = msgBody.getInteger("d", 0)
            var e = msgBody.getInteger("e", 0)
            var f = msgBody.getInteger("f", 0)
            var g = msgBody.getInteger("g", 0)
            calc(a, b, c, d, e, f, g){reply ->
                it.reply(reply)
            }
        }
    }

    fun calc(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, replyHandler: (String) -> Unit){
        webClient.get(7777, "pi", "/sub?a=$b&b=$c").send{
            if (it.succeeded()) {
                var `b-c` = it.result().bodyAsString().toInt()
                webClient.get(7777, "pi", "/add?a=$`b-c`&b=$d").send{
                    if (it.succeeded()) {
                        var `(b-c)+d` = it.result().bodyAsString().toInt()
                        webClient.get(7777, "pi", "/add?a=$a&b=$`(b-c)+d`").send{
                            if (it.succeeded()) {
                                var `a+(b-c)+d` = it.result().bodyAsString().toInt()
                                webClient.get(7777, "pi", "/sub?a=$`a+(b-c)+d`&b=$e").send{
                                    if (it.succeeded()) {
                                        var `a+(b-c)+d-e` = it.result().bodyAsString().toInt()
                                        webClient.get(7777, "pi", "/sub?a=$`a+(b-c)+d-e`&b=$f").send{
                                            if (it.succeeded()) {
                                                var `a+(b-c)+d-e-f` = it.result().bodyAsString().toInt()
                                                webClient.get(7777, "pi", "/add?a=$`a+(b-c)+d-e-f`&b=$g").send{
                                                    if (it.succeeded()) {
                                                        var `a+(b-c)+d-e-f+g` = it.result().bodyAsString().toInt()
                                                        replyHandler(`a+(b-c)+d-e-f+g`.toString())
                                                    } else {
                                                        replyHandler("calc error: a + (b - c) + d -e -f + g")
                                                    }
                                                }
                                            } else {
                                                replyHandler("calc error: a + (b - c) + d -e -f")
                                            }
                                        }

                                    } else {
                                        replyHandler("calc error: a + (b - c) + d -e")
                                    }
                                }
                            } else {
                                replyHandler("calc error: a + (b - c) + d")
                            }
                        }
                    } else {
                        replyHandler("calc error: (b - c) + d")
                    }
                }
            } else {
                replyHandler("calc error: b - c")
            }
        }
    }

}