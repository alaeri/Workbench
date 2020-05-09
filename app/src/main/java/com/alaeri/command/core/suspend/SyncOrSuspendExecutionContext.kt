//package com.alaeri.command.core.suspend
//
//import com.alaeri.command.CommandState
//import com.alaeri.command.core.SyncExecutionContext
//
//interface SyncOrSuspendExecutionContext<R>: SyncExecutionContext<R>,
//    SuspendingExecutionContext<R> {
//    override val owner: Any
//    override fun emit(commandState: CommandState<R>)
//}