import com.alaeri.command.history.id.DefaultIdStore
import com.alaeri.command.history.id.IndexAndUUID

fun Any.defaultKey() : IndexAndUUID = DefaultIdStore.instance.keyOf(this)
fun Any.defaultStringKey() : String = this.defaultKey().toString()