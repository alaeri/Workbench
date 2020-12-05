import com.alaeri.command.graph.component.Element
import com.alaeri.command.graph.serializedUnit
import com.alaeri.command.history.IdOwner
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableInvokationContext

fun <Key> SerializableInvokationContext<Key>.toElement() = Element(id, serializedClass)
fun <Key> IdOwner<Key>.toElement() : Element<Key>? = if(clazz != serializedUnit) { id?.let { id -> clazz?.let { Element(id, it) } } } else null
fun Element<IndexAndUUID>.toStr() = "$id $clazz"