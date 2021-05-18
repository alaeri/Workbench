package com.alaeri.cats.app.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alaeri.cats.app.ui.cats.CatsFragment
import com.alaeri.cats.app.ui.login.LoginFragment
import com.alaeri.log.android.ui.list.LogListFragment

class FragmentsAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment){

    private var currentList: List<Page> = listOf()

    override fun getItemId(position: Int): Long {
        return currentList[position].id.ordinal.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return currentList.any { it.id.ordinal.toLong() == itemId }
    }

    override fun getItemCount(): Int = currentList.size

    override fun createFragment(position: Int): Fragment {
        val page = currentList[position]
        return when(page.id){
            PageId.Login -> LoginFragment()
            PageId.Cats -> CatsFragment()
            PageId.LogList -> LogListFragment()
            else -> CatsFragment()
            //PageId.CommandsWebview -> com.alaeri.command.android.visualizer.graph.GraphFragment()
            //PageId.CommandsLifecycle -> com.alaeri.command.android.visualizer.option.VisualizationOptionsFragment()
        }
    }

    fun updatePages(newList: List<Page>){
        val diffCallback = PageDiffCallback(currentList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        currentList = newList
        diffResult.dispatchUpdatesTo(this)
    }
}