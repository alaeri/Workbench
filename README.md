# Workbench

[![Build Status](https://travis-ci.com/alaeri/Workbench.svg?branch=master)](https://travis-ci.com/alaeri/Workbench)
[![codecov](https://codecov.io/gh/alaeri/Workbench/branch/master/graph/badge.svg)](https://codecov.io/gh/alaeri/Workbench)
[![CodeFactor](https://www.codefactor.io/repository/github/alaeri/workbench/badge)](https://www.codefactor.io/repository/github/alaeri/workbench)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Falaeri%2FWorkbench.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Falaeri%2FWorkbench?ref=badge_shield)


## App

A sample app to experiment with. It displays cats from the CatApi. It also showcases use of the **Command Library** and **RecyclerView Extras**

## Command Library

Inline Kotlin Command Design Pattern Logger

[View the readme of the command-core module](/command-core/README.md)

## RecyclerView Extras

Some code based on discussions at work and after work.

## TODO:

* [X] Viewpager
* [X] Visualize add returned objects to visualization
* [X] LifecycleCommand
* [X] Add command tags
* [X] Find a cleaner command usage pattern for Koin
* [X] Configure focus
* [X] Repair graph
* [X] ConfigureCodeCoverage
* [X] Move the components to their own modules: command {core, android, koin, debug-ui, persistence}, recyclerview-helpers, catsApp { Koin, ManualDi } 
* [ ] Repair injection/unit test/
* [ ] Improve README
* [ ] Investigate async dependency injection patterns and create a manual injection version
* [ ] Logserver
* [ ] Logclient webpage
* [ ] FuzzingAnnotation : check if it already exists first.
* [ ] Move the recyclerview extras from their current package to a unique package name

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Falaeri%2FWorkbench.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Falaeri%2FWorkbench?ref=badge_large)
