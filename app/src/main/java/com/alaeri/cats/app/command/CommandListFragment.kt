package com.alaeri.cats.app.command

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.alaeri.cats.app.databinding.CatsFragmentBinding
import com.alaeri.command.history.SerializableCommandStateAndContext
import com.alaeri.command.history.id.IndexAndUUID
import org.koin.android.scope.lifecycleScope
import org.koin.android.viewmodel.scope.viewModel
import org.koin.core.KoinComponent

/**
 * Created by Emmanuel Requier on 05/05/2020.
 */
class CommandListViewModel(private val commandRepository: CommandRepository) : ViewModel(){
    private val mutableLiveData = MutableLiveData<List<SerializableCommandStateAndContext<IndexAndUUID>>>()
    val liveData:LiveData<List<SerializableCommandStateAndContext<IndexAndUUID>>>
        get() = mutableLiveData

    init {
        mutableLiveData.value = commandRepository.list.toList()
    }

    fun onRefresh(){
        mutableLiveData.value = commandRepository.list.toList()
    }
}
class CommandListFragment : Fragment(), KoinComponent {

    private lateinit var binding: CatsFragmentBinding
    //private val catsFragment : Fragment by lifecycleScope.inject { parametersOf(this) }
    private val commandListViewModel: CommandListViewModel by lifecycleScope.viewModel(this)
    private val adapter: CommandAdapter by lazy { lifecycleScope.get<CommandAdapter>() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CatsFragmentBinding.inflate(inflater)
        binding.apply {
            recyclerView.apply {
                adapter = this@CommandListFragment.adapter
                layoutManager = LinearLayoutManager(context)
            }
            swipeRefreshLayout.setOnRefreshListener { commandListViewModel.onRefresh() }
        }
        commandListViewModel.liveData.observe(this.viewLifecycleOwner, Observer {
            Log.d("CATS","$it")
            binding.apply {
                recyclerView.visibility = View.VISIBLE
                retryButton.visibility = View.INVISIBLE
                catsLoadingTextView.visibility = View.INVISIBLE
                progressCircular.hide()
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                retryButton.visibility = View.GONE
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        commandListViewModel.onRefresh()
    }
}