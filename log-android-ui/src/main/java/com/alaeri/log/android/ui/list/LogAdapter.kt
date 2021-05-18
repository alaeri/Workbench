package com.alaeri.log.android.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alaeri.log.android.ui.databinding.LogEmptyBinding
import com.alaeri.log.android.ui.databinding.LogItemBinding
import com.alaeri.log.android.ui.focus.FocusLogItemVM
import com.alaeri.log.android.ui.list.viewholder.EmptyVH
import com.alaeri.log.android.ui.list.viewholder.FocusVH
import com.alaeri.log.android.ui.list.viewholder.LogVH


class LogAdapter() : RecyclerView.Adapter<FocusVH<*>>(){

    val list: MutableList<FocusLogItemVM> = mutableListOf()
    companion object{
        const val viewTypeEmpty = 0
        const val viewTypeNotEmpty = 1
    }
    override fun getItemViewType(position: Int): Int = when(list[position]){
        is FocusLogItemVM.Empty -> viewTypeEmpty
        is FocusLogItemVM.Content -> viewTypeNotEmpty
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusVH<*> {
        return when(viewType) {
            viewTypeEmpty -> {
                val binding =
                    LogEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EmptyVH(binding)
            }
            else -> {
                val binding =
                    LogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LogVH(binding)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FocusVH<*>, position: Int) {
        val commandStateAndContext = list[position]
        holder.setFocusItem(commandStateAndContext)
    }



}