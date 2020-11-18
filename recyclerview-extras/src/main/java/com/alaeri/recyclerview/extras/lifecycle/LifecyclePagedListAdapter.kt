package com.alaeri.recyclerview.extras.lifecycle

import androidx.annotation.CallSuper
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */

abstract class LifecyclePagedListAdapter<T: Any, VH: BindAndAttachVH>: PagedListAdapter<T,VH> {

    constructor(asyncDifferConfig: AsyncDifferConfig<T>): super(asyncDifferConfig)
    constructor(diffItemCallback: DiffUtil.ItemCallback<T>): super(diffItemCallback)

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.isBound = true
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.isAttachedToWindow = true
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        holder.isAttachedToWindow = false
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.isBound = false
    }
}