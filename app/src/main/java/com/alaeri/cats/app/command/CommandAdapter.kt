package com.alaeri.cats.app.command

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.extras.viewholder.Bindable
import com.alaeri.cats.app.command.CommandVH.RandomColors.randomColors
import com.alaeri.cats.app.databinding.CommandItemBinding
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.history.serialization.SerializableCommandState
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.id.IndexAndUUID
import com.github.lzyzsd.randomcolor.RandomColor
import kotlin.random.Random

/**
 * Created by Emmanuel Requier on 05/05/2020.
 */
class CommandVH(private val commandItemBinding: CommandItemBinding): RecyclerView.ViewHolder(commandItemBinding.root), Bindable<SerializableCommandStateAndContext<IndexAndUUID>> {

    override fun setItem(item: SerializableCommandStateAndContext<IndexAndUUID>) {
        Log.d("CATS","show item: ${item.context.commandId.index}")
        commandItemBinding.apply {

            //commandItemBinding.root.setPadding(item.context.depth * 2, 2, 2,2)
            receiverTextView.setBackgroundColor(randomColors[item.context.executionContext.id.index])
            receiverTextView.text =
                "${item.context.executionContext}"
            invokerTextView.setBackgroundColor(randomColors[item.context.invokationContext.id.index])
            invokerTextView.text = item.context.invokationContext.toString()
            operationIdTextView.setBackgroundColor(randomColors[item.context.commandId.index])
            operationIdTextView.text = "${item.context.commandId} ${
                if (item.context.commandNomenclature != CommandNomenclature.Undefined) {
                    item.context.commandNomenclature::class.simpleName
                } else ""
            } ${item.context.commandName?:""}"
            val indexAndUUID = when(item.state){
                is SerializableCommandState.Value<IndexAndUUID> -> item.state.valueId
                is SerializableCommandState.Done<IndexAndUUID> -> item.state.valueId
                else -> null
            }
            indexAndUUID?.let {
                operationStateTextView.setBackgroundColor(randomColors[indexAndUUID.index])
            }
            operationStateTextView.text = item.state.shortString()
        }
    }

    object RandomColors{
        val rnd = Random(System.currentTimeMillis())
//        @ColorInt
        val randomColors: IntArray = IntRange(0, 200).map {
            Color.argb(255, rnd.nextInt(100), rnd.nextInt(100), rnd.nextInt(100)) }.toIntArray()
    }
}
class CommandAdapter() : RecyclerView.Adapter<CommandVH>(){

    val list: MutableList<SerializableCommandStateAndContext<IndexAndUUID>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandVH {
        val binding = CommandItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommandVH(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommandVH, position: Int) {
        val commandStateAndContext = list[position]
        holder.setItem(commandStateAndContext)
    }


}