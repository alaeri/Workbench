package com.alaeri.command.android.visualizer

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alaeri.command.CommandNomenclature
import com.alaeri.command.android.visualizer.CommandVH.RandomColors.randomColors
import com.alaeri.command.android.visualizer.databinding.CommandEmptyBinding
import com.alaeri.command.android.visualizer.databinding.CommandItemBinding
import com.alaeri.command.android.visualizer.focus.FocusCommandItemVM
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandState
import com.alaeri.recyclerview.extras.viewholder.Bindable
import kotlin.random.Random

/**
 * Created by Emmanuel Requier on 05/05/2020.
 */
abstract class FocusVH<T: FocusCommandItemVM>(view: View): RecyclerView.ViewHolder(view),
    Bindable<T> {
    fun setFocusItem(t: FocusCommandItemVM){
       (t as? T)?.let { setItem(it) }
    }
}
class CommandVH(private val commandItemBinding: CommandItemBinding): FocusVH<FocusCommandItemVM.Content>(commandItemBinding.root),
    Bindable<FocusCommandItemVM.Content> {


    override fun setItem(itemContainer: FocusCommandItemVM.Content) {
        val item = itemContainer.commandStateAndScope
        Log.d("CATS","show item: ${item.scope.commandId.index}")
        commandItemBinding.apply {

            //commandItemBinding.root.setPadding(item.context.depth * 2, 2, 2,2)
            receiverTextView.setBackgroundColor(item.scope.commandExecutionScope.id.color())
            receiverTextView.text =
                "${item.scope.commandExecutionScope}"
            invokerTextView.setBackgroundColor(item.scope.commandInvokationScope.id.color())
            invokerTextView.text = item.scope.commandInvokationScope.toString()
            parentOperationIdTextView.setBackgroundColor(item.scope.invokationCommandId.color())
            parentOperationIdTextView.text = "${item.scope.invokationCommandId}"
            operationIdTextView.setBackgroundColor(item.scope.commandId.color())
            operationIdTextView.text = "${item.scope.commandId} ${
                if (item.scope.commandNomenclature != CommandNomenclature.Undefined) {
                    item.scope.commandNomenclature::class.simpleName
                } else ""
            } ${item.scope.commandName?:""}"
            val stateIndexAndUUID = when(val state = item.state){
                is SerializableCommandState.Value<IndexAndUUID> -> state.valueId
                is SerializableCommandState.Done<IndexAndUUID> -> state.valueId
                else -> null
                //else -> item.context.commandId //if this state is not an idOwner we set this part of the cell to the same color than the commandId
            }
            if(stateIndexAndUUID != null){
                operationStateTextView.visibility = View.VISIBLE
                operationStateTextView.setBackgroundColor(stateIndexAndUUID.color())
            }else{
                operationStateTextView.visibility = View.GONE
            }

            operationStateTextView.text = item.state.shortString()
            operationStateTextView.setOnClickListener {
                stateIndexAndUUID?.let { itemContainer.onItemWithIdClicked(stateIndexAndUUID) }
            }
            receiverTextView.setOnClickListener {
                itemContainer.onItemWithIdClicked(item.scope.commandExecutionScope.id)
            }
            invokerTextView.setOnClickListener {
                itemContainer.onItemWithIdClicked(item.scope.commandInvokationScope.id)
            }
            parentOperationIdTextView.setOnClickListener {
                itemContainer.onItemWithIdClicked(item.scope.invokationCommandId)
            }
            operationIdTextView.setOnClickListener {
                itemContainer.onItemWithIdClicked(item.scope.commandId)
            }
            operationIdTextView
        }
        // FIXME repair padding
        // commandItemBinding.cardView. = (item.context.depth * 40).toFloat()
    }

    object RandomColors{
        val rnd = Random(System.currentTimeMillis())
//        @ColorInt
        val randomColors: IntArray = IntRange(0, 200).map {
            Color.argb(255, rnd.nextInt(100), rnd.nextInt(100), rnd.nextInt(100)) }.toIntArray()
    }

    fun IndexAndUUID.color()= randomColors[this.index % randomColors.size]
}
class EmptyVH(private val commandEmptyBinding: CommandEmptyBinding): FocusVH<FocusCommandItemVM.Empty>(commandEmptyBinding.root),
    Bindable<FocusCommandItemVM.Empty> {
    override fun setItem(item: FocusCommandItemVM.Empty) {
        commandEmptyBinding.textView.text = when(item){
            is FocusCommandItemVM.Empty.Break -> "${item.count} items filtered"
            is FocusCommandItemVM.Empty.End -> "${item.focusedCount} (${item.count}) items filtered"
        }
        val clickListener = when(item){
            is FocusCommandItemVM.Empty.Break -> item.onClearFocus
            is FocusCommandItemVM.Empty.End -> item.onClearRange
        }
        commandEmptyBinding.textView.setOnClickListener{ clickListener() }
    }
}

class CommandAdapter() : RecyclerView.Adapter<FocusVH<*>>(){

    val list: MutableList<FocusCommandItemVM> = mutableListOf()
    companion object{
        const val viewTypeEmpty = 0
        const val viewTypeNotEmpty = 1
    }
    override fun getItemViewType(position: Int): Int = when(list[position]){
        is FocusCommandItemVM.Empty -> viewTypeEmpty
        is FocusCommandItemVM.Content -> viewTypeNotEmpty
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusVH<*> {
        return when(viewType) {
            viewTypeEmpty -> {
                val binding =
                    CommandEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EmptyVH(binding)
            }
            else -> {
                val binding =
                    CommandItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CommandVH(binding)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FocusVH<*>, position: Int) {
        val commandStateAndContext = list[position]
        holder.setFocusItem(commandStateAndContext)
    }



}