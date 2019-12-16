package com.github.hongkaiwen.reactor.vk.verticle

import com.github.hongkaiwen.reactor.vk.controller.addStudentHandler
import com.github.hongkaiwen.reactor.vk.controller.calc
import com.github.hongkaiwen.reactor.vk.controller.calcVertx
import com.github.hongkaiwen.reactor.vk.controller.counter
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.client.sendAwait

class HttpVerticle : CoroutineVerticle() {

    lateinit var webClient: WebClient

    override suspend fun start() {
        webClient = WebClient.create(vertx)

        var router = Router.router(vertx)
        router.post("/add/student").handler(addStudentHandler)
        router.get("/counter").handler(counter)
        router.get("/calc").handler(calc)
        router.get("/pre/calc").handler(preCalc)


        vertx.createHttpServer().requestHandler(router).listenAwait(8888)
    }

    override suspend fun stop() {
        println("stop")
    }

    suspend fun sendHttpRequest(a: Int): String {
        val httpResponse = webClient.get(8888, "localhost", "/calc?i=$a").sendAwait()
        return httpResponse.bodyAsString()
    }

    var preCalc: (RoutingContext) -> Unit = { context ->
        var i = context.request().getParam("i")
        sendHttpRequest(i)
    }
}


