# Demo project of the structured log library using compose and compose-seqdiag

## First window: Demo

The demo is a jvm wikipedia browser in compose
You can use it to browse wikipedia and view the wiki page rendered in markdown.

You can view uses of the logging api in these files:

* [WikiViewModel](https://github.com/alaeri/Workbench/blob/master/log-seqdiag/src/main/java/com/alaeri/seqdiag/wiki/WikiViewModel.kt)
* [WikiRepository](https://github.com/alaeri/Workbench/blob/master/log-seqdiag/src/main/java/com/alaeri/seqdiag/wiki/WikiScreen.kt)
* [WikiScreen](https://github.com/alaeri/Workbench/blob/master/log-seqdiag/src/main/java/com/alaeri/seqdiag/wiki/data/WikiRepository.kt)

## Second Window: Log

The log is available in a second window and updates in real-time

a red arrow means that the execution is active.
a dashed arrow represent a flow OnEach event
older operations have arrows with lower opacity. (arrows fade over time)

![Screenshot 2023-01-22 at 15 43 08](https://user-images.githubusercontent.com/440667/213923532-fe4e60d3-2631-44ae-9512-ad69f40c19a9.png)


