package com.github.hongkaiwen.reactor.vk.verticle

import com.github.hongkaiwen.reactor.vk.controller.addStudentHandler
import com.github.hongkaiwen.reactor.vk.controller.calc
import com.github.hongkaiwen.reactor.vk.controller.counter
import com.github.hongkaiwen.reactor.vk.controller.preCalc
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class HttpVerticle : AbstractVerticle() {

    override fun start(promise: Promise<Void>) {
        var eb = vertx.eventBus()


        var server = vertx.createHttpServer()
        var router = Router.router(vertx)
        server.requestHandler(router)

        router.post("/add/student").handler(addStudentHandler)
        router.get("/counter").handler(counter)
        router.get("/calc").handler(calc)
        router.get("/pre/calc").handler(preCalc)
        //test curl http://192.168.1.168:8888/calc_func\?a\=1\&b\=2\&c\=3\&d\=4\&e\=5\&f\=6\&g\=7  should be return 0
        router.get("/calc_func").handler{
            var a = it.request().getParam("a").toInt()
            var b = it.request().getParam("b").toInt()
            var c = it.request().getParam("c").toInt()
            var d = it.request().getParam("d").toInt()
            var e = it.request().getParam("e").toInt()
            var f = it.request().getParam("f").toInt()
            var g = it.request().getParam("g").toInt()
            var msg = JsonObject()
            msg.put("a", a).put("b", b).put("c", c).put("d", d).put("e", e).put("f", f).put("g", g)
            eb.request<String>("calc.promise.line4", msg){ reply ->
                if (reply.succeeded()){
                    it.request().response().end(reply?.result()?.body() ?: "no response")
                } else {
                    reply.cause().printStackTrace()
                    it.request().response().end("calc failed")
                }
            }
        }


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
