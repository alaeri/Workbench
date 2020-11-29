package com.alaeri.command.graph

data class ElementConnection<Key>(val child: Element<Key>, val parent: Element<Key>)