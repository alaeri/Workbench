package com.alaeri

import com.alaeri.data.WikiRepositoryImpl
import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
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
object TuiBrowser {



    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }


    @JvmStatic
    fun main(args: Array<String>) {

        logBlocking<Unit>("start"){

            val terminal : Terminal = logBlocking(name = "createTerminal") {
                DefaultTerminalFactory().createTerminal()
            }
            val screen : TerminalScreen = logBlocking(name = "createTerminalScreen") {
                TerminalScreen(terminal)
            }
            val wikiRepository : WikiRepository = logBlocking(name= "createWikiRepository") {
                WikiRepositoryImpl()
            }
            val viewModelFactory : IViewModelFactory = logBlocking(name = "createViewModelFactory") {
                ViewModelFactory(wikiRepository)
            }
            val terminalScreen : TerminalAppScreen = logBlocking(name = "createTerminalAppScreen") {
                TerminalAppScreen(terminal, screen, viewModelFactory)
            }

            runBlocking {
               log(name = "launch command server") {
                    launch {
                        withContext(Dispatchers.IO){
                            println("server started")
                            SampleLogServer.start()
                        }
                    }

                }
                try{
                    terminalScreen.runAppAndWait()
                }catch (e: Exception){
                   println("exiting with exception...")
                   println(e)
                }
                SampleLogServer.quit()
            }
        }

        exitProcess(0)
    }
}




