package com.alaeri

import com.alaeri.command.*
import com.alaeri.command.core.invokeCommand
import com.alaeri.command.core.suspendInvoke
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.server.CommandServer
import com.alaeri.data.WikiRepositoryImpl
import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.IViewModelFactory
import com.alaeri.presentation.tui.TerminalAppScreen
import com.alaeri.presentation.wiki.ViewModelFactory
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.*
import java.util.*
import kotlin.system.exitProcess


@ExperimentalCoroutinesApi
object TuiBrowser: ICommandRootOwner {

    private val commandRepository = CommandRepository()
    private val idBank = IdBank<IndexAndUUID>(null){ previous ->
        IndexAndUUID(index = (previous?.index ?: 0) + 1, uuid = UUID.randomUUID())
    }
    private val commandLogger : DefaultIRootCommandLogger = Serializer<IndexAndUUID>(idBank, commandRepository)
    val aServer = CommandServer(commandRepository)

    private val logger: ILogger = object : ILogger {
        override fun println(s: Any?) {
            aServer.add(s.toString())
        }
    }

    override val commandRoot = buildCommandRoot(this, "tui-browser", CommandNomenclature.Root, commandLogger)


    @JvmStatic
    fun main(args: Array<String>) {

        invokeRootCommand<Unit>("start", CommandNomenclature.Application.Start){

            val terminal : Terminal = invokeCommand {
                DefaultTerminalFactory().createTerminal()
            }
            val screen : TerminalScreen = invokeCommand {
                TerminalScreen(terminal)
            }
            val wikiRepository : WikiRepository = invokeCommand {
                WikiRepositoryImpl(logger)
            }
            val viewModelFactory : IViewModelFactory = invokeCommand {
                ViewModelFactory(wikiRepository, logger, commandLogger)
            }
            val terminalScreen : TerminalAppScreen = invokeCommand {
                TerminalAppScreen(terminal, screen, logger, viewModelFactory)
            }
            invokeCommand<Unit,Unit> {
                aServer.start()
            }
            runBlocking {
                try{
                    suspendInvoke {
                        terminalScreen.runAppAndWait()
                    }
                }catch (e: Exception){
                    logger.println("exiting with exception...")
                    logger.println(e)
                }
                aServer.stop()
            }
        }

        exitProcess(0)
    }
}




