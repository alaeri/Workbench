package com.alaeri.log.android.ui.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alaeri.log.android.ui.focus.FocusLogItemVM
import com.alaeri.recyclerview.extras.viewholder.Bindable

/**
 * Created by Emmanuel Requier on 05/05/2020.
 */
abstract class FocusVH<T: FocusLogItemVM>(view: View): RecyclerView.ViewHolder(view),
    Bindable<T> {
    fun setFocusItem(t: FocusLogItemVM){
        (t as? T)?.let { setItem(it) }
    }
}