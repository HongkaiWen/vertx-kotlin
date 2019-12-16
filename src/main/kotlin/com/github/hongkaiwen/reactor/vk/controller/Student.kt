package com.github.hongkaiwen.reactor.vk.controller

import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

lateinit var studentVertx: Vertx

val addStudentHandler: (RoutingContext) -> Unit = {
    var ctx = studentVertx.orCreateContext
    println("isEventLoopContext: ${ctx.isEventLoopContext} isWorkerContext: ${ctx.isWorkerContext} isMultiThreadedWorkerContext: ${ctx.isMultiThreadedWorkerContext}")
    println(it.request())
    var current = ctx.get<Int>("num") ?: 0
    println("current $current")
    ctx.put("num", current + 1)
    it.request().bodyHandler { body -> it.request().response().end(body) }
    studentVertx.setTimer(5000) {
        println("do after 5 seconds")
    }
}