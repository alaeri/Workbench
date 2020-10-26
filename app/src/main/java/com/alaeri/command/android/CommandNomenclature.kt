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
    object Root : CommandNomenclature()
    object Test : CommandNomenclature()
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
            object Root: Lifecycle()
            object OnAttach: Lifecycle()
            object OnCreate: Lifecycle()
            object OnCreateView: Lifecycle()
            object OnViewCreated: Lifecycle()
            object OnStart: Lifecycle()
            object OnResume: Lifecycle()
            object OnPause: Lifecycle()
            object OnStop: Lifecycle()
            object OnDestroy: Lifecycle()
        }
        sealed class UserInteraction: Android(){
            object Click: Android()

        }
    }

    sealed class Application : CommandNomenclature() {
        sealed class Cats: Application(){
            object LoadImage: Cats()
            object BuildCatListMediatorLiveData : Cats()
            object RefreshList : Cats()
            object FirstNameSubmitted : Cats()
            object InitLoginMediator : Cats()
        }
    }
}