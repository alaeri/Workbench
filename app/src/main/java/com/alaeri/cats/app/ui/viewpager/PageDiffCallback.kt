package com.alaeri.cats.app.ui.viewpager

import androidx.recyclerview.widget.DiffUtil

class PageDiffCallback(private val oldList: List<Page>, private val newList: List<Page>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id === newList.get(newItemPosition).id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition] == newList[newPosition]
    }
}