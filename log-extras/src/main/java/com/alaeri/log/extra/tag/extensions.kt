//
//fun Any.buildDefaultLogContext(name: String): LogContext {
//    return
//}
//
//suspend fun Any.buildBlockingDefaultLogContext(name: String): LogContext {
//    val coroutineContext = currentCoroutineContext()
//    return CoroutineContextLogContext(coroutineContext) + CallSiteLogContext() + ReceiverLogContext(this) + NamedLogContext(name) + ThreadLogContext()
//}