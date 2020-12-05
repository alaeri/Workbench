package com.alaeri.command.graph

data class IdAndParents(val id: String, val parents: List<String>, val label: String = id)