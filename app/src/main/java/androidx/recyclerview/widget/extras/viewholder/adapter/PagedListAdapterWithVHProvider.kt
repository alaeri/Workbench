package androidx.recyclerview.widget.extras.viewholder.adapter

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.extras.viewholder.Bindable
import androidx.recyclerview.widget.extras.viewholder.ViewHolderProvider

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */

class PagedListAdapterWithVHProvider<T: Any, VH>: PagedListAdapter<T,VH>  where VH : RecyclerView.ViewHolder, VH: Bindable<T> {

    constructor(viewHolderProvider: ViewHolderProvider<T, VH>, asyncDifferConfig: AsyncDifferConfig<T>) : super(asyncDifferConfig){
        this.viewHolderProvider = viewHolderProvider
    }
    constructor(viewHolderProvider: ViewHolderProvider<T, VH>, diffCallback: DiffUtil.ItemCallback<T>)  : super(diffCallback){
        this.viewHolderProvider = viewHolderProvider
    }

    private val viewHolderProvider: ViewHolderProvider<T, VH>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = viewHolderProvider.createViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) = viewHolderProvider.bind(holder, getItem(position))

    override fun getItemViewType(position: Int): Int = viewHolderProvider.getItemViewType(getItem(position))

}