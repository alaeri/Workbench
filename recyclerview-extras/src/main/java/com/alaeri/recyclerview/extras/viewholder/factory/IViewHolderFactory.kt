package com.alaeri.recyclerview.extras.viewholder.factory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
interface IViewHolderFactory<T: Any, VH: RecyclerView.ViewHolder>{
    operator fun invoke(parent: ViewGroup): VH
    infix fun matches(value: Any?) : Boolean
}