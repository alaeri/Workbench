package com.alaeri.cats.app.ui.cats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alaeri.cats.app.LogOwner
import com.alaeri.cats.app.databinding.CatsFragmentBinding
import com.alaeri.cats.app.logBlocking
import com.alaeri.recyclerview.extras.toLifecycleAdapter
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf


class CatsFragment : KoinComponent, ScopeFragment(), LogOwner {

    private val catsFragment : Fragment by inject { parametersOf(this) }
    private val catsViewModel: CatsViewModel by viewModel()
    private val catsAdapter by inject<CatsAdapter>()

    private lateinit var binding: CatsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        assert(catsFragment != null)
        return logBlocking("inflate view"){
            binding = CatsFragmentBinding.inflate(inflater, container, false)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logBlocking("onViewCreated"){
            binding.apply {
                recyclerView.apply {
                    adapter = catsAdapter.toLifecycleAdapter()
                    layoutManager = LinearLayoutManager(context)
                }
                swipeRefreshLayout.setOnRefreshListener { catsViewModel.onRefreshTriggered() }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        catsViewModel.currentState.observe(viewLifecycleOwner, Observer {
            logBlocking("catFragmentState") {
                Log.d("CATS", "$it")
                when (val page = it.pagedListState) {
                    is PagedListState.Page -> {
                        catsAdapter.submitList(page.pagedList)
                        binding.apply {
                            recyclerView.visibility = View.VISIBLE
                            retryButton.visibility = View.INVISIBLE
                            catsLoadingTextView.visibility = View.INVISIBLE
                            progressCircular.hide()
                            when (val refreshState = it.refreshState) {
                                is NetworkState.Loading -> swipeRefreshLayout.isRefreshing = true
                                is NetworkState.Idle -> {
                                    swipeRefreshLayout.isRefreshing = false
                                    Toast.makeText(
                                        requireContext(),
                                        "Erreur lors du chargement de la page suivante",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    is PagedListState.Empty -> {
                        binding.apply {
                            swipeRefreshLayout.isRefreshing = false
                            recyclerView.visibility = View.INVISIBLE
                            catsLoadingTextView.visibility = View.VISIBLE
                            retryButton.visibility = View.VISIBLE
                            retryButton.setOnClickListener { catsViewModel.onRefreshTriggered() }
                            when (val refreshState = it.refreshState) {
                                is NetworkState.Loading -> {
                                    retryButton.isEnabled = false
                                    catsLoadingTextView.text = "Chargements des chats"
                                }
                                is NetworkState.Idle -> {
                                    retryButton.isEnabled = true
                                    progressCircular.hide()
                                    catsLoadingTextView.text =
                                        refreshState.exception?.message ?: "Aucun chat disponible"
                                }
                            }

                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        logBlocking("onResume"){
            catsViewModel.onRefreshTriggered()
        }
    }
}
