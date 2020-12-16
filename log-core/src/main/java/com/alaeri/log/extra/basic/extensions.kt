import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.basic.CallSiteLogContext
import com.alaeri.log.extra.basic.NamedLogContext
import com.alaeri.log.extra.basic.ObjectLogContext
import com.alaeri.log.extra.basic.ThreadLogContext

fun Any.buildDefaultLogContext(name: String): LogContext {
    return CallSiteLogContext() + ObjectLogContext(this) + NamedLogContext(name) + ThreadLogContext()
}