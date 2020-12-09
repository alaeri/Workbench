package com.alaeri.command.graph

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.graph.group.GroupedCommandsGraphMapper
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandState
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by Emmanuel Requier on 05/12/2020.
 */
class CommandsGraphMapperIntegrationTest {

    private val moshi: Moshi = Moshi.Builder()
        .add(CommandStateAdapter())
        .add(CommandNomenclatureAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    //TODO fix this adapter and provide it to the command server
    internal class CommandNomenclatureAdapter {
        @ToJson
        fun toJson(card: CommandNomenclature): String {
            return card.toString()
        }

        @FromJson
        fun fromJson(card: Any): CommandNomenclature {
            return CommandNomenclature.Undefined
        }
    }

    //TODO fix this adapter and provide it to the command server
    internal class CommandStateAdapter {
        @ToJson
        fun toJson(card: SerializableCommandState<IndexAndUUID>): String {
            return card.toString()
        }

        @FromJson
        fun fromJson(card: Any): SerializableCommandState<IndexAndUUID> {
            return SerializableCommandState.Waiting<IndexAndUUID>()
        }
    }

    private val file = File(ClassLoader.getSystemResource("commands.json").file)

    private val serializableCommandStateAndContextType: ParameterizedType = Types.newParameterizedType(
        SerializableCommandStateAndScope::class.java,
        IndexAndUUID::class.java
    )
    private val type : Type = Types.newParameterizedType(
        List::class.java,
        serializableCommandStateAndContextType
    )


    private val jsonAdapter : JsonAdapter<List<SerializableCommandStateAndScope<IndexAndUUID>>> = moshi.adapter(
        type
    );


    private val commands = jsonAdapter.fromJson(file.readText())!!

    @Test
    fun testReadingJsonWorks(){
        assertTrue(file.exists())
        assertEquals(597, commands.size)
    }

    //TODO create a new JSON file with the new serialization names
    @Test
    fun testGraphMappingWorks(){
        val data = GroupedCommandsGraphMapper.buildGraph(commands)
        val levels = data.levels
        assertEquals(8, data.levels.size)
        levels.forEachIndexed { index, level ->
            println("level: $index")
            level.forEach{
                println("${it.id}: ${it.label} --> ${it.parents}")
            }
        }
    }



}