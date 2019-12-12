package com.github.hongkaiwen.reactor.vk.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class HttpVertcle : AbstractVerticle() {

  val addStudentHandler: (RoutingContext) -> Unit = {
    var ctx = vertx.orCreateContext
    println("isEventLoopContext: ${ctx.isEventLoopContext} isWorkerContext: ${ctx.isWorkerContext} isMultiThreadedWorkerContext: ${ctx.isMultiThreadedWorkerContext}")
    println(it.request())
    var current = ctx.get<Int>("num") ?: 0
    println("current $current")
    ctx.put("num", current + 1)
    it.request().response().end("ok")
    vertx.setTimer(5000) {
      println("do after 5 seconds")
    }
  }

  var counterData = HashMap<String, Int>()

  val counter: (RoutingContext) -> Unit = {
    var name = it.request().getParam("name")
    var current = counterData.get(name) ?: 0
    current ++
    counterData.put(name, current)
    it.request().response().end("counter")
  }

  override fun start(promise: Promise<Void>) {
    var server = vertx.createHttpServer()
    var router = Router.router(vertx)
    server.requestHandler(router)

    router.post("/add/student").handler(addStudentHandler)
    router.get("/counter").handler(counter)


    server.listen(8888) { result ->
      if (result.succeeded()) {
        promise.complete()
        println("bind to 8888")
      } else {
        promise.fail(result.cause())
      }
    }

    vertx.setPeriodic(1000) {
      var tmpData = counterData
      counterData = HashMap()
      for ((k, v) in tmpData) {
        println("k is $k, v is $v")
      }
    }.let { println("counter started.") }
  }

  override fun stop() {
    println("stop")
  }
}
