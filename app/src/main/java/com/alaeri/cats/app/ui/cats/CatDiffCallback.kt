package com.alaeri.cats.app.ui.cats

import androidx.recyclerview.widget.DiffUtil
import com.alaeri.cats.app.cats.Cat

class CatDiffCallback: DiffUtil.ItemCallback<Cat>() {
    override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean = oldItem == newItem
}