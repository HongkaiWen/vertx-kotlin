package com.github.hongkaiwen.reactor.vk.controller

import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

private var counterData = HashMap<String, Int>()

val counter: (RoutingContext) -> Unit = {
    var name = it.request().getParam("name")
    var current = (counterData[name] ?: 0) + 1
    counterData[name] = current
    it.request().response().end("counter")
    var c = Vertx.currentContext()
}

fun counterStatics(){
    Vertx.vertx().setPeriodic(1000) {
        var tmpData = counterData
        counterData = HashMap()
        for ((k, v) in tmpData) {
            println("k is $k, v is $v")
        }
    }.let { println("counter started.") }
}

