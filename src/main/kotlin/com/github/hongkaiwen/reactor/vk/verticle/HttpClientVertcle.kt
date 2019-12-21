package com.github.hongkaiwen.reactor.vk.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Context
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient

class HttpClientVertcle : AbstractVerticle() {

    lateinit var webClient: WebClient

    override fun init(vertx: Vertx?, context: Context?) {
        super.init(vertx, context)
        webClient = WebClient.create(vertx)
    }

    override fun start(startPromise: Promise<Void>?) {
        var eb = vertx.eventBus()
        var mc = eb.consumer<Int>("hello")

        mc.handler { message ->
            sendHttpRequest(message.body()).future().apply { "result is $this" }
                .onSuccess{ message.reply(it) }.onFailure { message.reply(it.message) }
        }


    }

    fun sendHttpRequest(a: Int) : Promise<String> {
        var promise = Promise.promise<String>()

        webClient.get(8888, "192.168.1.168", "/calc?i=$a").send {
            if (it.succeeded()) {
                if(it.result().statusCode() != 200){
                    promise.fail("error code ${it.result().statusCode()}")
                    println("not 200")
                } else {
                    promise.complete(it.result().bodyAsString())
                    println("response ${it.result().bodyAsString()}")
                }
            } else {
                promise.fail(it.cause())
                println(it.cause())
            }
        }

        return promise
    }

}
