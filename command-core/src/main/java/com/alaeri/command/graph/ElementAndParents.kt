package com.alaeri.command.graph

data class ElementAndParents<Key>(val element: Element<Key>, val parents: List<Element<Key>>)