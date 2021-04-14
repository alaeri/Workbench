package com.alaeri.log.server

import com.alaeri.log.repository.GraphRepository
import com.alaeri.log.repository.LogRepository
import com.alaeri.log.serialize.serialize.SerializedTag
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class LogServer(
    private val graphRepository: GraphRepository,
    private val logRepository: LogRepository) {


    private val server = embeddedServer(Netty, 8080) {

        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                disableHtmlEscaping()
                registerTypeAdapter(SerializedTag::class.javaObjectType, SerializedTagAdapter)
            }
        }
        install(WebSockets)

        install(StatusPages){
            exception<Throwable> { cause ->
                println("exception: $cause")
                cause.stackTrace.forEach {
                    println(it)
                }
            }
        }

        routing {
            //trace { application.log.trace(it.buildText()) }
            webSocket("/ws/") {
                graphRepository.graph.collect {
                    outgoing.offer(Frame.Text(Gson().toJson(it)))
                }
            }
            get("clear"){
                TODO("implement filter/clear methods")
            }
            get("logs"){
                val list  = logRepository.listAsFlow.first()
                call.respond(list)
            }
            get("graph"){
                val levels = graphRepository.graph.first()
                call.respond(levels)
            }
            //TODO find a way to make this work better than having to use a shell script
            //Maybe use a fallback files thingy?
            static("/") {
                resources("graph")
                defaultResource( "graph/d3graph.html")
            }
        }
    }

    @EngineAPI
    fun start() : Unit = server.start(false).let {}

    fun stop() =  server.stop(0, 0, TimeUnit.SECONDS)

}