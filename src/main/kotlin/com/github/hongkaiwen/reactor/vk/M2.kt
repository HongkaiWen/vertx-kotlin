package com.github.hongkaiwen.reactor.vk

import io.vertx.core.Future
import io.vertx.core.Promise
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

fun main() {
   var f = calc()
    f.thenAccept { println(it) }
}

fun calc(): CompletableFuture<Int> {
    return CompletableFuture.supplyAsync {
        TimeUnit.SECONDS.sleep(3)
        5
    }
}

fun <T, R> compose(f1: Future<T>, f2: () -> Future<R>): Future<R>{
    var promise = Promise.promise<R>()
    f1.onComplete{
        if (it.succeeded()) {
            var ff = f2()
            ff.setHandler(promise)
        } else {
            promise.fail(it.cause())
        }
    }
    return promise.future()
}