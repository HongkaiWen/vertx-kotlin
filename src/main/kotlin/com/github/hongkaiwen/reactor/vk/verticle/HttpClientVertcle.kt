package com.github.hongkaiwen.reactor.vk.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message

class HttpClientVertcle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>?) {
        var eb = vertx.eventBus()
        var mc = eb.consumer<Int>("hello")
        mc.handler {
            sendHttpRequest(it.body(), it)
        }
    }

    fun sendHttpRequest(a: Int, msg: Message<Int>) {
        var client = vertx.createHttpClient()
        client.get(8888, "localhost", "/calc?i=$a").handler { response ->
            response.bodyHandler {
                msg.reply(String(it.bytes))
            }
        }.end()
    }

}
