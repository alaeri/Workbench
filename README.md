# Workbench - Kotentin?

[![Build Status](https://travis-ci.com/alaeri/Workbench.svg?branch=master)](https://travis-ci.com/alaeri/Workbench)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Falaeri%2FWorkbench.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Falaeri%2FWorkbench?ref=badge_shield)

This project contains an implementation of *ViewHolderFactory*, a *LifecycleAdapter* and a *LifecycleViewHolder*, it also contains an implementation of a Command Design Pattern.

I began this project as a toyproject to experiment around the CleanArch and hierarchical usecases. 
I also wanted to try the paging library and Koin.
I used the CatsApi to do so.
I realized half-way there that the best way to do it would be to visualize the application execution and that it was worth trying to do it with Kotlin.
I realized half-way there that I was recreating a Command Design Pattern so I tried to implement the pattern

## Command Design Pattern - Koticot - KoCoDePaIn

Kotlin Command Design Pattern Inline

![Latest visualization](/doc/device-2020-09-07-012143.png)

### Commands and Contexts: the code and the extension functions to use it.

### Serialization

### Visualization

## RecyclerView Extras

Some code based on discussions at work and after work.

### LifecycleAdapter / LifecycleViewholder

TBC

### ViewHolderFactory

TBC

## TODO:

* [X] Viewpager
* [X] Visualize add returned objects to visualization
* [X] LifecycleCommand
* [X] Add command tags
* [X] Find a cleaner command usage pattern for Koin
* [X] Configure focus
* [X] Repair graph
* [ ] Repair injection/unit test/
* [ ] ConfigureCodeCoverage
* [ ] Move the components to their own modules: command {core, android, koin, debug-ui, persistence}, recyclerview-helpers, catsApp { Koin, ManualDi } 
* [ ] Improve README
* [ ] Investigate async dependency injection patterns and create a manual injection version
* [ ] Logserver
* [ ] Logclient webpage
* [ ] FuzzingAnnotation : check if it already exists first.
* [ ] Move the recyclerview extras from their current package to a unique package name

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Falaeri%2FWorkbench.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Falaeri%2FWorkbench?ref=badge_large)