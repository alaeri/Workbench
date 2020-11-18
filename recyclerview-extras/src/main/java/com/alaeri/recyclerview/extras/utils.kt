package com.alaeri.recyclerview.extras

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alaeri.recyclerview.extras.lifecycle.BindAndAttachVH

/**
 * Created by Emmanuel Requier on 26/04/2020
 *
 *
 */
fun <VH: BindAndAttachVH> RecyclerView.Adapter<VH>.toLifecycleAdapter(): RecyclerView.Adapter<VH> {

    val internalAdapter = this

    return object: RecyclerView.Adapter<VH>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = internalAdapter.onCreateViewHolder(parent, viewType)
        override fun getItemCount(): Int = internalAdapter.itemCount

        override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>){
            Log.d("CATS","onBindViewHolder payloads")
            holder.isBound = true
            internalAdapter.onBindViewHolder(holder, position, payloads)
        }

        override fun onBindViewHolder(holder: VH, position: Int){
            Log.d("CATS","onBindViewHolder")
            holder.isBound = true
            internalAdapter.onBindViewHolder(holder, position)

        }
        override fun onViewAttachedToWindow(holder: VH) {
            Log.d("CATS","onViewAttachedToWindow")
            holder.isAttachedToWindow = true
            internalAdapter.onViewAttachedToWindow(holder)

        }
        override fun onViewDetachedFromWindow(holder: VH) {
            holder.isAttachedToWindow = false
            internalAdapter.onViewDetachedFromWindow(holder)
            Log.d("CATS","onViewDetachedFromWindow")

        }
        override fun onViewRecycled(holder: VH) {
            Log.d("CATS","onViewRecycled")
            internalAdapter.onViewRecycled(holder)
            holder.isBound = false
        }

        override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) = internalAdapter.unregisterAdapterDataObserver(observer)

        override fun getItemId(position: Int): Long = internalAdapter.getItemId(position)

        override fun setHasStableIds(hasStableIds: Boolean) = internalAdapter.setHasStableIds(hasStableIds)

        override fun onFailedToRecycleView(holder: VH): Boolean = internalAdapter.onFailedToRecycleView(holder)

        override fun getItemViewType(position: Int): Int = internalAdapter.getItemViewType(position)

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) = internalAdapter.onAttachedToRecyclerView(recyclerView)

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) =internalAdapter.onDetachedFromRecyclerView(recyclerView)

        override fun findRelativeAdapterPositionIn(
            adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
            viewHolder: RecyclerView.ViewHolder,
            localPosition: Int
        ): Int = internalAdapter.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition)

        override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) = internalAdapter.registerAdapterDataObserver(observer)

        //override fun canRestoreState(): Boolean = internalAdapter.canRestoreState()

        override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
            internalAdapter.stateRestorationPolicy = strategy
        }
    }
}
