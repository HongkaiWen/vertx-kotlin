package com.github.hongkaiwen.reactor.vk.coroutine

import com.github.hongkaiwen.reactor.vk.calc.CalcOperator
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.web.client.sendAwait
import kotlinx.coroutines.launch

class CoroutineHttpVerticle : CoroutineVerticle(){

    lateinit var webClient: WebClient


    override suspend fun start() {
        webClient = WebClient.create(vertx)

        var server = vertx.createHttpServer()
        var router = Router.router(vertx)

        router.get("/calc_func").coroutineHandler{calc(it)}

        server.requestHandler(router)
        server.listenAwait(8888)
        println("server started on ${server.actualPort()}")
    }

    suspend fun calc(context: RoutingContext){
        var a = context.request().getParam("a").toInt()
        var b = context.request().getParam("b").toInt()
        var c = context.request().getParam("c").toInt()
        var d = context.request().getParam("d").toInt()
        var e = context.request().getParam("e").toInt()
        var f = context.request().getParam("f").toInt()
        var g = context.request().getParam("g").toInt()

        var result = a asyncAdd ((b asyncSub c) asyncAdd d) asyncSub e asyncSub f asyncAdd g
        context.request().response().end(result.toString())
    }

    suspend infix fun Int.asyncAdd(input : Int) : Int {
        return calc(this, input, CalcOperator.add)
    }

    suspend infix fun Int.asyncSub(input : Int) : Int {
        return calc(this, input, CalcOperator.sub)
    }

    suspend fun calc(a: Int, b: Int, operator: CalcOperator) : Int {
        return webClient.get(7777, "pi", "/${operator.name}?a=$a&b=$b")
            .expect(ResponsePredicate.SC_OK).sendAwait().bodyAsString().toInt()

    }

    /**
     * An extension method for simplifying coroutines usage with Vert.x Web routers
     */
    fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
        handler { ctx ->
            launch(ctx.vertx().dispatcher()) {
                try {
                    fn(ctx)
                } catch (e: Exception) {
                    ctx.fail(e)
                }
            }
        }
    }

}