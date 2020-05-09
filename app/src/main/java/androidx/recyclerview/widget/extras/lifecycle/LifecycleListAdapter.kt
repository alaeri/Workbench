package androidx.recyclerview.widget.extras.lifecycle

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Emmanuel Requier on 26/04/2020.
 */
abstract class LifecycleListAdapter<T, VH: BindAndAttachVH>: ListAdapter<T, VH>{
    constructor(diffCallback: DiffUtil.ItemCallback<T>, asyncDifferConfig: AsyncDifferConfig<T>) :super(asyncDifferConfig)
    constructor(diffCallback: DiffUtil.ItemCallback<T>): super(diffCallback)

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