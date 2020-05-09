package com.alaeri.cats.app.ui.cats

sealed class NetworkState{
    object Loading: NetworkState()
    data class Idle(val exception: Exception? = null): NetworkState()
}