package androidx.recyclerview.widget.extras.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.extras.viewholder.factory.IViewHolderFactory

class ViewHolderProvider<T: Any, VH>(private val factories: List<IViewHolderFactory<T, VH>>) where VH: RecyclerView.ViewHolder, VH: Bindable<T>{

    constructor(vararg factories: IViewHolderFactory<T, VH>): this(factories.toList())

    fun getItemViewType(item: T?): Int = factories.indexOfFirst { it matches item }

    fun createViewHolder(parent: ViewGroup, viewType: Int) : VH = factories[viewType].invoke(parent)

    fun bind(holder: VH, item: T?) {
        item?.let{ holder.setItem(it) }
    }

}