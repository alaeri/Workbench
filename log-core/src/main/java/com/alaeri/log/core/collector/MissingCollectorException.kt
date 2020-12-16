package com.alaeri.log.core.collector

import java.lang.Exception

class MissingCollectorException: Exception(defaultErrorMessage){
    companion object{
        const val defaultErrorMessage = "No collector defined: use NoopCollector if you do not want to define one at this stage"
    }
}