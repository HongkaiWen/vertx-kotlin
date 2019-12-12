package com.github.hongkaiwen.reactor.vk

import com.github.hongkaiwen.reactor.vk.verticle.HttpVertcle
import io.vertx.core.Vertx

fun main() {

  var vertx = Vertx.vertx()

  vertx.deployVerticle(HttpVertcle()){
    if (it.succeeded()){
      println("success deploy http vertcle")
    } else {
      println(it.cause())
    }
  }
}
