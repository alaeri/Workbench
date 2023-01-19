package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation

sealed class HistoryItem{
    data class Actor(val id: IdentityRepresentation, val name: String) : HistoryItem()

    data class Line(val from: IdentityRepresentation, val to: IdentityRepresentation, val name: String): HistoryItem()
}



data class GraphRepresentation(val items: List<HistoryItem>)