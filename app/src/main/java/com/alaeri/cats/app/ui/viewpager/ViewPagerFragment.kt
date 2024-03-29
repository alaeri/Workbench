package com.alaeri.cats.app.ui.viewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alaeri.cats.app.R
import com.alaeri.cats.app.databinding.ViewpagerFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.util.Collections.max
import java.util.Collections.min

enum class PageId{
    Login,
    Cats,
    LogList,
    LogsWebview,
    LogOptions
}
data class Page(val id: PageId)

class ViewPagerFragment : ScopeFragment(), KoinComponent {

    private var binding: ViewpagerFragmentBinding? = null
    private val fragmentsAdapter by lazy { FragmentsAdapter(this) }
    //private val viewPagerModel: ViewPagerViewModel by lifecycleScope.viewModel(this)
    private lateinit var tabLayoutMediator : TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val viewPagerFragmentInjected : ViewPagerFragment by lifecycleScope.inject { parametersOf(this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ViewpagerFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            pager.adapter = fragmentsAdapter
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragment: Fragment by inject<ViewPagerFragment>{ parametersOf(this) }
        val viewPagerModel by viewModel<ViewPagerViewModel>()
        viewPagerModel.pages.observe(viewLifecycleOwner, Observer {
            fragmentsAdapter.updatePages(it)
            tabLayoutMediator = TabLayoutMediator(binding!!.tabs, binding!!.pager) { tab, position ->
                val resId = when(it[position].id){
                    PageId.Cats -> R.string.cats_fragment_title
                    PageId.Login -> R.string.login_fragment_title
                    PageId.LogList -> R.string.logs_list_fragment_title
                    PageId.LogsWebview -> R.string.graph_fragment_title
                    PageId.LogOptions -> R.string.commands_lifecycle_fragment_title
                }
                tab.text = getString(resId)
            }
            tabLayoutMediator.attach()
        })
        viewPagerModel.focused.observe(viewLifecycleOwner, Observer { focusState ->
            binding?.focus?.apply {
                if(focusState.focused != null){
                    focusedCommandTextView.text = "${focusState.focused.index} - ${focusState.focused.uuid}"
                    clearButton.setOnClickListener{ focusState.clearFocus() }
                    focusedCommandLayout.visibility = View.VISIBLE
                } else {
                    focusedCommandLayout.visibility = View.GONE
                }
                timeRangeSlider.apply {
                    Log.d("CATS", "focusState: $focusState")
                    valueTo = Float.MAX_VALUE
                    valueFrom = Float.MIN_VALUE
                    values = listOf(focusState.start, focusState.end)
                    valueFrom = focusState.minStart
                    valueTo = focusState.maxEnd

                }
            }

        })
        binding?.focus?.timeRangeSlider?.apply {
            addOnChangeListener { slider, _, fromUser ->
                if(fromUser){
                    val start = min(slider.values)
                    val end = max(slider.values)
                    viewPagerModel.onTimeRangeChanged(start, end)
                }
            }
        }
    }
}

