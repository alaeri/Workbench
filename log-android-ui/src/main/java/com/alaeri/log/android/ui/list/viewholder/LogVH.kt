package com.alaeri.log.android.ui.list.viewholder

import android.graphics.Color
import android.view.View
import com.alaeri.log.android.ui.databinding.LogItemBinding
import com.alaeri.log.android.ui.focus.FocusLogItemVM
import com.alaeri.log.android.ui.list.viewholder.LogVH.RandomColors.randomColors
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.callsite.CallSiteRepresentation
import com.alaeri.log.extra.tag.coroutine.CoroutineContextLogRepresentation
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.extra.tag.thread.ThreadRepresentation
import com.alaeri.log.serialize.serialize.EmptySerializedTag
import com.alaeri.log.serialize.serialize.SerializedLogMessage
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation
import com.alaeri.recyclerview.extras.viewholder.Bindable
import kotlin.random.Random

class LogVH(private val logItemBinding: LogItemBinding): FocusVH<FocusLogItemVM.Content>(logItemBinding.root),
    Bindable<FocusLogItemVM.Content> {

//    Start
//    [Parent information]
//    [Current thread] - [Parameters] -


    override fun setItem(itemContainer: FocusLogItemVM.Content) {
        val item = itemContainer.commandStateAndScope
        val tag = item.tag
        logItemBinding.apply {
            operationIdTextView.setBackgroundColor(tag.identity.color())
        }
        when(tag){
            is ListRepresentation -> {
                tag.representations.forEach{ rep ->
                    when(rep) {
                        is CallSiteRepresentation -> {}
                        is CoroutineContextLogRepresentation -> {
                            logItemBinding.apply {
                                threadTextView.text = rep.identity.toString()
                                threadTextView.setBackgroundColor(rep.identity.color())
                            }
                        }
                        is ThreadRepresentation -> {
                            logItemBinding.apply {
                                threadTextView.text = rep.name
                                threadTextView.setBackgroundColor(rep.identity.color())
                            }
                        }
                        is EmptySerializedTag -> {}
                        is FiliationRepresentation -> {
                            logItemBinding.apply {
                                val listRep = rep.parentRepresentation as? ListRepresentation
                                val invoker: ReceiverRepresentation? =
                                    listRep?.representations?.filterIsInstance<ReceiverRepresentation>()
                                        ?.firstOrNull()
                                if(invoker != null && listRep != null){
                                    invokerTextView.visibility = View.VISIBLE
                                    parentOperationIdTextView.visibility = View.VISIBLE
                                    parentOperationIdTextView.setBackgroundColor(rep.parentRepresentation.identity.color())
                                    parentOperationIdTextView.text =
                                        listRep?.representations?.filterIsInstance<NameRepresentation>()
                                            ?.firstOrNull()?.name
                                    invokerTextView.setBackgroundColor(invoker?.identity?.color() ?: 0)
                                    invokerTextView.text = invoker?.type?.clazz?.name
                                }else{
                                    invokerTextView.visibility = View.GONE
                                    parentOperationIdTextView.visibility = View.GONE
                                }
                            }
                        }
                        is ListRepresentation -> {

                        }
                        is NameRepresentation -> {
                            logItemBinding.apply{
                                operationIdTextView.text = rep.name
                            }
                        }
                        is ReceiverRepresentation -> {
                            logItemBinding.apply {
                                receiverTextView.text = rep.type.clazz.name
                                receiverTextView.setBackgroundColor(rep.identity.color())
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
        val message = item.message
        when(message){
            is SerializedLogMessage.Start -> {
                logItemBinding.apply {
                    if(message.parameters.isNotEmpty()){
                        operationStateTextView.visibility = View.VISIBLE
                        operationStateTextView.text = "start with ${message.parameters}"
                        //operationStateTextView.setBackgroundColor(stateIndexAndUUID.color())
                    }else{
                        operationStateTextView.visibility = View.GONE
                    }
                }
            }
            is SerializedLogMessage.Error -> {
                logItemBinding.apply {
                    if(message.throwableRepresentation !=  null){
                        operationStateTextView.visibility = View.VISIBLE
                        operationStateTextView.text = "error: with ${message.throwableRepresentation}"
                        //operationStateTextView.setBackgroundColor(stateIndexAndUUID.color())
                    }else{
                        operationStateTextView.visibility = View.GONE
                    }
                }
            }
            is SerializedLogMessage.Success -> {
                logItemBinding.apply {
                    if(message.entityRepresentation !=  null){
                        operationStateTextView.visibility = View.VISIBLE
                        operationStateTextView.text = "error: with ${message.entityRepresentation}"
                        //operationStateTextView.setBackgroundColor(stateIndexAndUUID.color())
                    }else{
                        operationStateTextView.visibility = View.GONE
                    }
                }
            }
        }


//        Log.d("CATS","show item: ${item.scope.commandId.index}")
//        logItemBinding.apply {
//
//            //commandItemBinding.root.setPadding(item.context.depth * 2, 2, 2,2)
//
//            receiverTextView.text =
//                "${item.scope.commandExecutionScope}"

//

//            val stateIndexAndUUID = when(val state = item.state){
//                is SerializableCommandState.Value<IndexAndUUID> -> state.valueId
//                is SerializableCommandState.Done<IndexAndUUID> -> state.valueId
//                else -> null
//                //else -> item.context.commandId //if this state is not an idOwner we set this part of the cell to the same color than the commandId
//            }
//
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