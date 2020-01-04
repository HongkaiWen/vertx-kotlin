//package com.github.hongkaiwen.reactor.vk.coroutine
//
//import com.github.hongkaiwen.reactor.vk.calc.CalcOperator
//import io.vertx.core.json.JsonObject
//import io.vertx.ext.web.client.WebClient
//import io.vertx.ext.web.client.predicate.ResponsePredicate
//import io.vertx.kotlin.coroutines.CoroutineVerticle
//import io.vertx.kotlin.ext.web.client.sendAwait
//
///**
// * example for calc: a + (（b -c）+ d) -e -f + g
// * by coroutine compare to reactor and promise
// */
//class CalcVerticle : CoroutineVerticle(){
//
//    lateinit var webClient: WebClient
//
//    override suspend fun start() {
//        webClient = WebClient.create(vertx)
//
//        var eventBus = vertx.eventBus()
//
//        eventBus.consumer<JsonObject>("calc.reactor"){ msg ->
//            var msgBody = msg.body()
//            var a = msgBody.getInteger("a", 0)
//            var b = msgBody.getInteger("b", 0)
//            var c = msgBody.getInteger("c", 0)
//            var d = msgBody.getInteger("d", 0)
//            var e = msgBody.getInteger("e", 0)
//            var f = msgBody.getInteger("f", 0)
//            var g = msgBody.getInteger("g", 0)
//
//            a asyncAdd ((b asyncSub c) asyncAdd d)
//
//        }
//
//    }
//
//    suspend infix fun Int.asyncAdd(input : Int) : Int {
//        return calc(this, input, CalcOperator.add)
//    }
//
//    suspend infix fun Int.asyncSub(input : Int) : Int {
//        return calc(this, input, CalcOperator.sub)
//    }
//
//
//    /**
//     * 所有异常必须被处理
//     */
//    suspend fun calc(a: Int, b: Int, operator: CalcOperator) : Int {
//        return webClient.get(7777, "pi", "/${operator.name}?a=$a&b=$b")
//            .expect(ResponsePredicate.SC_OK).sendAwait().bodyAsString().toInt()
//
//    }
//}
