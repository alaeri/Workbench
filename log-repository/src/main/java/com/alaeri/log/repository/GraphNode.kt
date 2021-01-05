package com.alaeri.log.repository

data class GraphNode(val id: String, val parents: List<String>, val label: String = id)