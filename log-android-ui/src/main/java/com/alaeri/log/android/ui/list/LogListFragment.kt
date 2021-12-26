package com.alaeri.log.android.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alaeri.log.android.ui.databinding.LogListFragmentBinding
import com.alaeri.log.android.ui.focus.LogFocusViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * Created by Emmanuel Requier on 17/05/2021.
 */
class LogListFragment: Fragment(), KoinComponent {

    private lateinit var logListViewModel: LogFocusViewModel
    private lateinit var adapter: LogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val injected: LogFocusViewModel by inject()
        logListViewModel = injected
        //val fragment: Fragment by lifecycleScope.inject { parametersOf(this@LogListFragment) }
        val injectedAdapter : LogAdapter by inject()
        adapter = injectedAdapter
        val binding = LogListFragmentBinding.inflate(inflater).apply {
            recyclerView.apply {
                adapter = this@LogListFragment.adapter
                layoutManager = LinearLayoutManager(context)
            }
            swipeRefreshLayout.setOnRefreshListener { /*commandListViewModel.onRefresh()*/ }
        }

        logListViewModel.liveData.observe(this@LogListFragment.viewLifecycleOwner, {
            Log.d("CATS", "display items on card: $it")
            binding.apply {
                recyclerView.visibility = View.VISIBLE
                retryButton.visibility = View.GONE
                catsLoadingTextView.visibility = View.GONE
                progressCircular.hide()
                adapter
                adapter.list.clear()
                swipeRefreshLayout.isRefreshing = it.isComputing
                adapter.list.addAll(it.list)
                adapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                retryButton.visibility = View.GONE
            }
        })
        return binding.root
    }

}