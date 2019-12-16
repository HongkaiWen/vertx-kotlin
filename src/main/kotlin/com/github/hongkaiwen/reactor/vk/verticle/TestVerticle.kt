package com.github.hongkaiwen.reactor.vk.verticle

import io.vertx.core.AbstractVerticle

class TestVerticle : AbstractVerticle() {

    override fun start() {
        var eb = vertx.eventBus()
        var mc = eb.consumer<String>("haha")
        mc.handler { println(it.body()) }
    }

}