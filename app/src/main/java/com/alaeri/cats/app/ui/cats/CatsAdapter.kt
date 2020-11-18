package com.alaeri.cats.app.ui.cats

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.alaeri.cats.app.cats.Cat
import com.alaeri.recyclerview.extras.viewholder.ViewHolderProvider

class CatsAdapter(
    private val viewHolderProvider: ViewHolderProvider<Cat, CatItemVH>,
    diffCallback: DiffUtil.ItemCallback<Cat> = CatDiffCallback(),
    asyncDifferConfig: AsyncDifferConfig<Cat> = AsyncDifferConfig.Builder<Cat>(diffCallback).build()
): PagedListAdapter<Cat, CatItemVH>(asyncDifferConfig){

    override fun getItemViewType(position: Int): Int {
        return viewHolderProvider.getItemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = viewHolderProvider.createViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: CatItemVH, position: Int) {
        val cat = getItem(position)
        if(cat != null){
            holder.setItem(cat)
        }
    }
}