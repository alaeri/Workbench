package com.alaeri.log.core

//Singleton that provides the current LogDataAndCollector for each thread calling
object BlockingLogContextStore{

    val threadLocal = ThreadLocal<LogEnvironment>()


}