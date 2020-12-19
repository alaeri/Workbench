import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.context.basic.CallSiteLogContext
import com.alaeri.log.extra.context.basic.NamedLogContext
import com.alaeri.log.extra.context.basic.ObjectLogContext
import com.alaeri.log.extra.context.basic.thread.ThreadLogContext

fun Any.buildDefaultLogContext(name: String): LogContext {
    return CallSiteLogContext() + ObjectLogContext(this) + NamedLogContext(name) + ThreadLogContext()
}