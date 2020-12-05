package com.alaeri.command.graph.component

data class ElementConnection<Key>(val child: Element<Key>, val parent: Element<Key>)