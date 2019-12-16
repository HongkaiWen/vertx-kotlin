package com.github.hongkaiwen.reactor.vk.verticle

import com.github.hongkaiwen.reactor.vk.controller.addStudentHandler
import com.github.hongkaiwen.reactor.vk.controller.calc
import com.github.hongkaiwen.reactor.vk.controller.counter
import com.github.hongkaiwen.reactor.vk.controller.preCalc
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class HttpVerticle : AbstractVerticle() {

    override fun start(promise: Promise<Void>) {
        var server = vertx.createHttpServer()
        var router = Router.router(vertx)
        server.requestHandler(router)

        router.post("/add/student").handler(addStudentHandler)
        router.get("/counter").handler(counter)
        router.get("/calc").handler(calc)
        router.get("/pre/calc").handler(preCalc)


        server.listen(8888) { result ->
            if (result.succeeded()) {
                promise.complete()
                println("bind to 8888")
            } else {
                promise.fail(result.cause())
            }
        }
    }

    override fun stop() {
        println("stop")
    }
}
