package com.github.hongkaiwen.reactor.vk.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Context
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
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
        mc.handler {
            sendHttpRequest(it.body(), it)
        }
    }

    fun sendHttpRequest(a: Int, msg: Message<Int>) {
        webClient.get(8888, "localhost", "/calc?i=$a").send {
            if (it.succeeded()) {
                println(it.result())
                msg.reply(it.result().body().toString())
            } else {
                println(it.cause())
            }
        }
    }

}
