package com.alaeri.tui_browser

import com.alaeri.tui_browser.wiki.WikiRepository
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.system.exitProcess


@ExperimentalCoroutinesApi
object TuiBrowser {

    private val mutableList = mutableListOf<Any?>()
    private val logger: ILogger = object : ILogger{
        override fun println(s: Any?) {
            mutableList.add(s)
        }
    }


    @JvmStatic
    fun main(args: Array<String>) {
        val terminal = DefaultTerminalFactory().createTerminal()
        val screen = TerminalScreen(terminal)
        val wikiRepository = WikiRepository(logger)
        val viewModelFactory = ViewModelFactory(wikiRepository, logger)
        val terminalScreen = TermminalAppScreen(terminal, screen, logger, viewModelFactory)

        runBlocking {
            try{
                terminalScreen.runAppAndWait()
            }catch (e: Exception){
                logger.println(e)
            }

            mutableList.forEach { println(it) }
            exitProcess(0)
        }
    }
}




