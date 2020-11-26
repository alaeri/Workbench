package com.alaeri

import com.alaeri.data.WikiRepositoryImpl
import com.alaeri.domain.ILogger
import com.alaeri.presentation.tui.TermminalAppScreen
import com.alaeri.presentation.wiki.ViewModelFactory
import com.alaeri.domain.wiki.WikiRepository
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.*
import kotlin.system.exitProcess


@ExperimentalCoroutinesApi
object TuiBrowser {

    private val mutableList = mutableListOf<Any?>()
    private val logger: ILogger = object : ILogger {
        override fun println(s: Any?) {
            mutableList.add(s)
        }
    }


    @JvmStatic
    fun main(args: Array<String>) {
        val terminal = DefaultTerminalFactory().createTerminal()
        val screen = TerminalScreen(terminal)
        val wikiRepository = WikiRepositoryImpl(logger)
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




