package com.github.hongkaiwen.reactor.vk

import com.github.hongkaiwen.reactor.vk.controller.calcVertx
import com.github.hongkaiwen.reactor.vk.controller.counterStatics
import com.github.hongkaiwen.reactor.vk.controller.studentVertx
import com.github.hongkaiwen.reactor.vk.verticle.TestVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx


fun main() {

  var vertx = Vertx.vertx()

//  var vertx2 = Vertx.vertx()

  calcVertx = vertx
  studentVertx = vertx

  var server = Promise.promise<String>()
  println(server)
  var client = Promise.promise<String>()

  vertx.deployVerticle(HttpClientVertcle(), client)
  vertx.deployVerticle("com.github.hongkaiwen.reactor.vk.verticle.HttpVerticle", DeploymentOptions().setInstances(2), server)
  vertx.deployVerticle(TestVerticle())

  vertx.setPeriodic(1000){
    vertx.eventBus().send("haha", "caca")
  }

  println("event loop count ${vertx.nettyEventLoopGroup().count()}")

  counterStatics()

  server.future().onComplete {
    it.succeeded()
  }

  CompositeFuture.all(server.future(), client.future())
    .setHandler {
      if (it.succeeded()) {
        println("start success")
      } else {
        println("start failed")
        vertx.close()
      }
    }
}
