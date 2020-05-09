package com.alaeri.cats.app.command

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.extras.viewholder.Bindable
import com.alaeri.cats.app.command.CommandVH.RandomColors.randomColors
import com.alaeri.cats.app.databinding.CommandItemBinding
import com.alaeri.command.history.SerializableCommandState
import com.alaeri.command.history.SerializableCommandStateAndContext
import com.alaeri.command.history.id.IndexAndUUID
import com.github.lzyzsd.randomcolor.RandomColor

/**
 * Created by Emmanuel Requier on 05/05/2020.
 */
class CommandVH(private val commandItemBinding: CommandItemBinding): RecyclerView.ViewHolder(commandItemBinding.root), Bindable<SerializableCommandStateAndContext<IndexAndUUID>> {

    override fun setItem(item: SerializableCommandStateAndContext<IndexAndUUID>) {
        commandItemBinding.apply {

            //commandItemBinding.root.setPadding(item.context.depth * 2, 2, 2,2)
            receiverTextView.setTextColor(randomColors[item.context.executionContext.id.index])
            receiverTextView.text = item.context.executionContext.toString()
            invokerTextView.setTextColor(randomColors[item.context.invokationContext.id.index])
            invokerTextView.text = item.context.invokationContext.toString()
            operationIdTextView.setTextColor(randomColors[item.context.commandId.index])
            operationIdTextView.text = item.context.commandId.toString()
            val indexAndUUID = when(item.state){
                is SerializableCommandState.Value<IndexAndUUID> -> item.state.valueId
                is SerializableCommandState.Done<IndexAndUUID> -> item.state.valueId
                else -> null
            }
            indexAndUUID?.let {
                operationStateTextView.setTextColor(randomColors[indexAndUUID.index])
            }
            operationStateTextView.text = item.state.toString()
        }
    }

    object RandomColors{
        val randomColors: IntArray = RandomColor().randomColor(1000)
    }
}
class CommandAdapter() : RecyclerView.Adapter<CommandVH>(){

    val list: MutableList<SerializableCommandStateAndContext<IndexAndUUID>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandVH {
        val binding = CommandItemBinding.inflate(LayoutInflater.from(parent.context))
        return CommandVH(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CommandVH, position: Int) {
        val commandStateAndContext = list[position]
        holder.setItem(commandStateAndContext)
    }


}