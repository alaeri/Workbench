# Command Library

Command library is a kotlin library that uses flows and inline functions to provide a way to illustrate the execution of code
It is intended as an almost transparent hierarchical logging framework. 
You code your project almost as usual but you wrap some function bodies with the extensions functions and you will see everything there is to know about their execution.
A bit like if java stacktraces could be recorded during code execution with a small impact on performance
This project does not use reflection.

## Goal

Some of the building blocks we use to make apps are complex. (Glide, Dagger, Paging library, Room)
They do heavy work to simplify entire domains for developers but they often are black boxes.
Can we make a library that allows **developers** and **users** to visualize what is happening when they use an application or a library. 

### Extensions

* [Command-Koin](../command-koin/) Extensions for wrapping the DI Koin api
* [Command-Android](../command-android/) Extensions for lifecycleowners so that they can become Command EntryPoints (ICommandRoot)
* [Command-Android-Visualizer](../command-android-visualizer) Visualization Fragments
* [Command-Glide](command-glide) Very bad wrapper around glide.

## Picture of the current state of the project

![Latest visualization](/doc/device-2020-09-07-012143.png)

Replace with a video

## Design goal

What should be visible:
* dependency graphs,
* function calls starts and transit of observed values

#### Illustrations from Android documentation to use as inspirations:

The images below come from [Android documentation website](https://developer.android.com/): 

![Android doc example](/doc/androiddoc1.png)
![Android doc example](/doc/androiddoc2.png)
![Android doc example](/doc/androiddoc3.png)
![Android doc example](/doc/androiddoc4.png)



## Nomenclature

This nomenclature is far from being finalized. The naming issues are twofold:

* Naming for classes and interfaces and functions used in this implementation
* Naming for common commands

### Nomenclature of common tasks

We will need some coherence for logging between projects so that there are default names available for common tasks and that users of the project might understand what is going on.

Most apps will use:
* injection related tasks
* persistence related tasks
* network related tasks
* lifecyle related tasks

The library should provide a sane extensible Nomenclature for these common tasks



### Naming for the api.

Some of these elements should be renamed as Scopes? - not sure yet when it should be a CommandContext vs a CommandScope
If you understand what this library is doing and are familiar with kotlinx.coroutines and or Dagger, please share your thoughts on the naming.

* ICommandRoot : entry point to invoke a command. Should maybe renamed to CommandEntryPoint to imitate Hilt naming.
* ICommand: a command w
* IInvokationContext: The scope in which a command is invoked. (Calling scope)
* IExecutionContext: The scope in which the content of a command is executed.

All these elements have two versions, a synchronized version and a suspending version.

## Naming of the project

* Koticot ?
* KoCoDePaIn ?
* Kotentin ?

## Test Coverage

Test coverage should be extensive before this is used in production.

## Benchmark

A benchmark should be available so that users know the impact this will have on performance.

## History

I began this project during the lockdown as a toyproject to experiment around the CleanArch and hierarchical usecases. 
I also wanted to try the paging library and Koin and view cats.
I realized half-way there that the best way to do it would be to visualize the application execution and that it was worth trying to do it with Kotlin.
I realized half-way there that I was recreating a Command Design Pattern so I tried to implement the pattern
