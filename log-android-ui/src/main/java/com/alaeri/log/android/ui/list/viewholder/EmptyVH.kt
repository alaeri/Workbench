package com.alaeri.log.android.ui.list.viewholder

import com.alaeri.log.android.ui.databinding.LogEmptyBinding
import com.alaeri.log.android.ui.focus.FocusLogItemVM
import com.alaeri.recyclerview.extras.viewholder.Bindable

class EmptyVH(private val commandEmptyBinding: LogEmptyBinding): FocusVH<FocusLogItemVM.Empty>(commandEmptyBinding.root),
    Bindable<FocusLogItemVM.Empty> {
    override fun setItem(item: FocusLogItemVM.Empty) {
        commandEmptyBinding.textView.text = when(item){
            is FocusLogItemVM.Empty.Break -> "${item.count} items filtered"
            is FocusLogItemVM.Empty.End -> "${item.focusedCount} (${item.count}) items filtered"
        }
        val clickListener = when(item){
            is FocusLogItemVM.Empty.Break -> item.onClearFocus
            is FocusLogItemVM.Empty.End -> item.onClearRange
        }
        commandEmptyBinding.textView.setOnClickListener{ clickListener() }
    }
}