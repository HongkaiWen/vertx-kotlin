package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class SleepVerticle : AbstractVerticle(){

    override fun start(startPromise: Promise<Void>?) {
        var r = test()
        vertx.setTimer(3000) {
            r.future().onSuccess {
                println("promise success")
            }
        }
    }

    fun test(): Promise<Unit>{
        var r = Promise.promise<Unit>()
        r.complete()
        return r
    }

}