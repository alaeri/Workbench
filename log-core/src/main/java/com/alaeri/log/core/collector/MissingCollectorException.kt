package com.alaeri.log.core.collector

import com.alaeri.log.core.Log
import java.lang.Exception

class MissingCollectorException(private val tag: Log.Tag): Exception("No collector defined: use NoopCollector if you do not want to define one at this stage $tag"){
}