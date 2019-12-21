package com.github.hongkaiwen.reactor.vk.controller

import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

lateinit var calcVertx: Vertx

var calc: (RoutingContext) -> Unit = {
    var i = it.request().getParam("i")
    println("receive request $i")
    i = i.toInt().times(i.toInt()).toString()
    it.request().response().end(i)
    println(i)
}

var preCalc: (RoutingContext) -> Unit = { context ->
    var i = context.request().getParam("i")
    calcVertx.eventBus().request<String>("hello", i.toInt()) {
        if (it.succeeded()) {
            context.request().response().end(it?.result()?.body() ?: "no response")
        } else {
            context.request().response().end("500")
        }
    }
}

