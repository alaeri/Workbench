package com.alaeri.log.android.ui.list.viewholder

import android.graphics.Color
import com.alaeri.log.android.ui.databinding.LogItemBinding
import com.alaeri.log.android.ui.focus.FocusLogItemVM
import com.alaeri.log.android.ui.list.viewholder.LogVH.RandomColors.randomColors
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.recyclerview.extras.viewholder.Bindable
import kotlin.random.Random

class LogVH(private val logItemBinding: LogItemBinding): FocusVH<FocusLogItemVM.Content>(logItemBinding.root),
    Bindable<FocusLogItemVM.Content> {


    override fun setItem(itemContainer: FocusLogItemVM.Content) {
        val item = itemContainer.commandStateAndScope
//        Log.d("CATS","show item: ${item.scope.commandId.index}")
//        logItemBinding.apply {
//
//            //commandItemBinding.root.setPadding(item.context.depth * 2, 2, 2,2)
//            receiverTextView.setBackgroundColor(item.scope.commandExecutionScope.id.color())
//            receiverTextView.text =
//                "${item.scope.commandExecutionScope}"
//            invokerTextView.setBackgroundColor(item.scope.commandInvokationScope.id.color())
//            invokerTextView.text = item.scope.commandInvokationScope.toString()
//            parentOperationIdTextView.setBackgroundColor(item.scope.invokationCommandId.color())
//            parentOperationIdTextView.text = "${item.scope.invokationCommandId}"
//            operationIdTextView.setBackgroundColor(item.scope.commandId.color())
//            operationIdTextView.text = "${item.scope.commandId} ${
//                if (item.scope.commandNomenclature != CommandNomenclature.Undefined) {
//                    item.scope.commandNomenclature::class.simpleName
//                } else ""
//            } ${item.scope.commandName?:""}"
//            val stateIndexAndUUID = when(val state = item.state){
//                is SerializableCommandState.Value<IndexAndUUID> -> state.valueId
//                is SerializableCommandState.Done<IndexAndUUID> -> state.valueId
//                else -> null
//                //else -> item.context.commandId //if this state is not an idOwner we set this part of the cell to the same color than the commandId
//            }
//            if(stateIndexAndUUID != null){
//                operationStateTextView.visibility = View.VISIBLE
//                operationStateTextView.setBackgroundColor(stateIndexAndUUID.color())
//            }else{
//                operationStateTextView.visibility = View.GONE
//            }
//
//            operationStateTextView.text = item.state.shortString()
//            operationStateTextView.setOnClickListener {
//                stateIndexAndUUID?.let { itemContainer.onItemWithIdClicked(stateIndexAndUUID) }
//            }
//            receiverTextView.setOnClickListener {
//                itemContainer.onItemWithIdClicked(item.scope.commandExecutionScope.id)
//            }
//            invokerTextView.setOnClickListener {
//                itemContainer.onItemWithIdClicked(item.scope.commandInvokationScope.id)
//            }
//            parentOperationIdTextView.setOnClickListener {
//                itemContainer.onItemWithIdClicked(item.scope.invokationCommandId)
//            }
//            operationIdTextView.setOnClickListener {
//                itemContainer.onItemWithIdClicked(item.scope.commandId)
//            }
//            operationIdTextView
        }
        // FIXME repair padding
        // commandItemBinding.cardView. = (item.context.depth * 40).toFloat()

    object RandomColors{
        val rnd = Random(System.currentTimeMillis())
        //        @ColorInt
        val randomColors: IntArray = IntRange(0, 200).map {
            Color.argb(255, rnd.nextInt(100), rnd.nextInt(100), rnd.nextInt(100))
        }.toIntArray()
    }

    fun IdentityRepresentation.color()= randomColors[this.index % randomColors.size]
}