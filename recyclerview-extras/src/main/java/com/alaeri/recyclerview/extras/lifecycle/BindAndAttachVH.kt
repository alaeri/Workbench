package com.alaeri.recyclerview.extras.lifecycle

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 */
abstract class BindAndAttachVH(itemView: View) : RecyclerView.ViewHolder(itemView){
    abstract var isAttachedToWindow: Boolean
    abstract var isBound: Boolean
}