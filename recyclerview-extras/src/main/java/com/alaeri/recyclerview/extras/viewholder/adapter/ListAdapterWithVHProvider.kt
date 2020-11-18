package com.alaeri.recyclerview.extras.viewholder.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alaeri.recyclerview.extras.viewholder.Bindable
import com.alaeri.recyclerview.extras.viewholder.ViewHolderProvider


/**
 * Created by Emmanuel Requier on 26/04/2020.
 */
class ListAdapterWithVHProvider<T:Any, VH>: ListAdapter<T, VH> where VH : RecyclerView.ViewHolder, VH: Bindable<T> {

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