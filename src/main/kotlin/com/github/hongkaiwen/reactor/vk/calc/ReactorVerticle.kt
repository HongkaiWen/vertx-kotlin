package com.github.hongkaiwen.reactor.vk.calc

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import reactor.core.publisher.Mono

class ReactorVerticle : AbstractVerticle(){

    lateinit var webClient: WebClient

    override fun start() {
        webClient = WebClient.create(vertx)
        var eventBus = vertx.eventBus()

        eventBus.consumer<JsonObject>("calc.reactor"){ msg ->
            var msgBody = msg.body()
            var a = msgBody.getInteger("a", 0)
            var b = msgBody.getInteger("b", 0)
            var c = msgBody.getInteger("c", 0)
            var d = msgBody.getInteger("d", 0)
            var e = msgBody.getInteger("e", 0)
            var f = msgBody.getInteger("f", 0)
            var g = msgBody.getInteger("g", 0)

            (b asyncSub c)
                .filter{ it > 5 }
                .switchIfEmpty(Mono.just(6))
                .flatMap { it asyncAdd d }
                .flatMap { it asyncAdd a }
                .flatMap { it asyncSub e }
                .flatMap { it asyncSub f }
                .flatMap { it asyncAdd g }
                .doOnError {  msg.fail(500, it.message) }
                .subscribe { msg.reply(it.toString()) }

        }

    }

    infix fun Int.asyncAdd(input : Int) : Mono<Int> {
        return calc(this, input, CalcOperator.add)
    }

    infix fun Int.asyncSub(input : Int) : Mono<Int> {
        return calc(this, input, CalcOperator.sub)
    }

    /**
     * 所有异常必须被处理
     */
    fun calc(a: Int, b: Int, operator: CalcOperator) : Mono<Int> {

        return Mono.create { sink ->
            webClient.get(7777, "pi", "/${operator.name}?a=$a&b=$b")
                .expect(ResponsePredicate.SC_OK).send{
                    if (it.succeeded()) {
                        try{
                            var addResult = it.result().bodyAsString().toInt()
                            sink.success(addResult)
                            println("reactor calc: $a - $b = $addResult")
                        } catch (e: Exception) {
                            sink.error(e)
                        }
                    } else {
                        sink.error(it.cause())
                    }
                }
        }

    }

}