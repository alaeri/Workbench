package com.alaeri.recyclerview.extras.viewholder.factory

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewHolderFactory<T: Any, VH: RecyclerView.ViewHolder, VB : ViewBinding>(
    val matcher : (instance : Any?) -> Boolean,
    val inflater: (layoutInflater: LayoutInflater, parent: ViewGroup, boolean: Boolean) -> VB,
    val builder: (viewBinding: VB, parent: ViewGroup)-> VH
    ):
    IViewHolderFactory<T, VH> {

    override operator fun invoke(parent: ViewGroup) : VH {
        val viewbinding = inflater(LayoutInflater.from(parent.context), parent, false)
        return builder(viewbinding, parent)
    }

    override fun matches(value: Any?): Boolean = value != null && matcher(value)
    companion object {
        inline fun <reified T> buildMatcher(): (Any?)-> Boolean{
            return { any ->
                isType<T>(
                    any
                )
            }
        }
        inline fun <reified T> isType(any: Any?): Boolean{
            return any is T
        }
        inline fun <reified T: Any, VH: RecyclerView.ViewHolder, VB: ViewBinding> newInstance(
            noinline inflater: (layoutInflater: LayoutInflater, parent: ViewGroup, boolean: Boolean) -> VB,
            noinline builder: (viewBinding: VB, parent: ViewGroup)-> VH
        ): ViewHolderFactory<T, VH, VB> {
            return ViewHolderFactory(
                buildMatcher<T>(),
                inflater,
                builder
            )
        }
    }
}


