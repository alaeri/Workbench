package com.alaeri.command.graph

data class GraphNode(val id: String, val parents: List<String>, val label: String = id)