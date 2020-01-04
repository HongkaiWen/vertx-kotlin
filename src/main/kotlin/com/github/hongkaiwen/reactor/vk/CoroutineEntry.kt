package com.github.hongkaiwen.reactor.vk

import com.github.hongkaiwen.reactor.vk.coroutine.CoroutineHttpVerticle
import io.vertx.core.Vertx

fun main() {
    var vertx = Vertx.vertx()
    vertx.deployVerticle(CoroutineHttpVerticle())
}