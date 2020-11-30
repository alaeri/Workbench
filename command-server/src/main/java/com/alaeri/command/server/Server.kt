package com.alaeri.command.server

import com.alaeri.command.core.Command
import com.alaeri.command.core.command
import com.alaeri.command.graph.CommandsToGraphRepresentationMapper
import com.alaeri.command.history.ICommandRepository
import com.alaeri.command.history.id.IndexAndUUID
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class CommandServer(val commandRepository: ICommandRepository<IndexAndUUID>) {

    private val mutableList = mutableListOf<Any?>()

    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {}
        }
        install(WebSockets)

        routing {
            webSocket("/ws/") {
                commandRepository.commands.map {
                    CommandsToGraphRepresentationMapper.buildLevels(it)
                }.collect {
                    outgoing.offer(Frame.Text(Gson().toJson(it)))
                }
            }
            get("logs") {
                call.respond(mutableList)
            }
            get("commands"){
                val list = runBlocking {
                    commandRepository.commands.first()
                }
                call.respond(list)
            }
            get("graph"){
                val list = runBlocking {
                    val levels = commandRepository.commands.first().let {
                        CommandsToGraphRepresentationMapper.buildLevels(it)
                    }
                    call.respond(levels)
                }
                call.respond(list)
            }
            static("/") {
                resource("/", "test/d3graph.html")
                static("/") {
                    resources("test")
                }
            }
        }
    }

    fun start() : Command<Unit> = command {
        server.start(false)
    }

    fun stop(): Command<Unit> = command {
       server.stop(0, 0, TimeUnit.SECONDS)
    }

    fun add(toString: String) {
        mutableList.add(toString)
    }

}