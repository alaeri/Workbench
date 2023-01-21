package com.alaeri.seqdiag.wiki

import com.alaeri.seqdiag.wiki.data.LoadingStatus
import java.io.PrintWriter
import java.io.StringWriter

fun stacktracePrettyPrint(e: Exception): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    return sw.toString()
}

fun LoadingStatus.toMarkdown() : String = when(this){
    is LoadingStatus.Loading -> "#### ${this.step} ${this.input}"
    is LoadingStatus.Error -> "#### Error while ${this.step} ${this.input} \n\n" +
            "${this.error.message} \n\n" +
            stacktracePrettyPrint(this.error)
    is LoadingStatus.Success -> this.markdown
}