package com.alaeri.command.android

/**
 * This file contains the system of tagging using for commands, for filtering
 *
 * What is the best representation for this?
 * Should it work with a generic type?
 * What are the operations we want to apply on this?
 * How can each app customize it to its domain and share the common stuff?
 */
sealed class CommandNomenclature {
    object Undefined: CommandNomenclature()
    object Root : CommandNomenclature() {

    }

    sealed class Injection : CommandNomenclature(){
        object Initialization: Injection()
        object Creation: Injection()
        object Retrieval: Injection()
    }
    sealed class Remote: CommandNomenclature(){

    }
    sealed class Persistence: CommandNomenclature(){

    }
    sealed class Cache: CommandNomenclature(){

    }
    sealed class Runtime : CommandNomenclature(){

    }
    sealed class Android: CommandNomenclature(){
        sealed class Lifecycle: Android(){
            object OnAttach: Android()
            object OnCreate: Android()
            object OnCreateView: Android()
            object OnViewCreated: Android()
            object OnStart: Android()
            object OnResume: Android()
            object OnPause: Android()
            object OnStop: Android()
            object OnDestroy: Android()
        }
        sealed class UserInteraction: Android(){
            object Click: Android()

        }
    }
}