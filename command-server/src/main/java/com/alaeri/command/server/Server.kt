package com.alaeri.command.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.concurrent.TimeUnit

class CommandServer {

    private val mutableList = mutableListOf<Any?>()

    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {}
        }
        routing {
            get("logs") {
                call.respond(mutableList)
            }
            static("/") {
                resource("/", "test/d3graph.html")
                static("/") {
                    resources("test")
                }
            }
        }
    }

    fun start(){
        server.start(false)
    }

    fun stop(){
       server.stop(0, 0, TimeUnit.SECONDS)
    }

    fun add(toString: String) {
        mutableList.add(toString)
    }

}