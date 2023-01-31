# Workbench for a log library

[![Build Status](https://travis-ci.com/alaeri/Workbench.svg?branch=master)](https://travis-ci.com/alaeri/Workbench)
[![codecov](https://codecov.io/gh/alaeri/Workbench/branch/master/graph/badge.svg)](https://codecov.io/gh/alaeri/Workbench)
[![CodeFactor](https://www.codefactor.io/repository/github/alaeri/workbench/badge)](https://www.codefactor.io/repository/github/alaeri/workbench)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Falaeri%2FWorkbench.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Falaeri%2FWorkbench?ref=badge_shield)


## Log Library

Structured Logger based on the command design pattern, implemented with inline functions and flows.
It allows the user to view the program execution in real-time.

Some illustrations generated from the lib are available in documentation module

-------------

### Main areas of development of the log library

#### Log api and implementation
  
What is the best api we can design and implement to build structured logs? 

#### Log configuration in project and subprojects

How can we enable or disable logging in a sample project and its dependencies?

#### Log performance

What is the performance impact of having logs enabled? 
What kind of benchmarks can we use?

#### Logs exploration and visualization

What is the easiest way to understand how a program is working?
Should the visualisation be included inside the app or be available in a webservice associated to the app.

* SequenceDiagrams
* Chronological log list
* Some other visualization ?

-----

## Samples

### App

A sample app to experiment with. It displays cats from the CatApi. It also showcases the use of the **Log Library** and **RecyclerView Extras**

### Sample-lib

Sample wikipedia markdown library to showcase the use of the log api in a library with faster development iteration time.

### Log-SeqDiag

Compose sample to browse wikipedia + visualization of the log library. (Flow logging is ~working~ in this sample)

### Tui-browser

TUI app to browse wikipedia and showcase the log lib.

### Mpp-Sample

Sample terminal app to showcase the use of the log api in an app with faster development iteration time.

### Log-koin



-----

## 

-----

## Misc

### RecyclerView Extras

Some code based on discussions at work and after work.



## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Falaeri%2FWorkbench.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Falaeri%2FWorkbench?ref=badge_large)
