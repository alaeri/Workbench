package com.alaeri

import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.command.Serializer
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.server.CommandServer
import com.alaeri.data.WikiRepositoryImpl
import com.alaeri.domain.ILogger
import com.alaeri.presentation.tui.TerminalAppScreen
import com.alaeri.presentation.wiki.ViewModelFactory
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.*
import java.util.*
import kotlin.system.exitProcess


@ExperimentalCoroutinesApi
object TuiBrowser {

    private val commandRepository = CommandRepository()
    private val idBank = IdBank<IndexAndUUID>(null){ previous ->
        IndexAndUUID(index = (previous?.index ?: 0) + 1, uuid = UUID.randomUUID())
    }
    val commandLogger : DefaultIRootCommandLogger = Serializer<IndexAndUUID>(idBank, commandRepository)
    val aServer = CommandServer(commandRepository)

    private val logger: ILogger = object : ILogger {
        override fun println(s: Any?) {
            aServer.add(s.toString())
        }
    }


    @JvmStatic
    fun main(args: Array<String>) {
        val terminal = DefaultTerminalFactory().createTerminal()
        val screen = TerminalScreen(terminal)
        val wikiRepository = WikiRepositoryImpl(logger)
        val viewModelFactory = ViewModelFactory(wikiRepository, logger)
        val terminalScreen = TerminalAppScreen(terminal, screen, logger, viewModelFactory)

        aServer.start()
        runBlocking {
            try{
                terminalScreen.runAppAndWait()
            }catch (e: Exception){
                logger.println("exiting with exception...")
                logger.println(e)
            }
            aServer.stop()
        }
        exitProcess(0)
    }
}




