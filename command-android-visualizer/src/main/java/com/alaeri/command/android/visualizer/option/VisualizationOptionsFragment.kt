package com.alaeri.command.android.visualizer.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alaeri.command.CommandState
import com.alaeri.command.ICommandLogger
import com.alaeri.command.android.LifecycleCommandContext
import com.alaeri.command.android.LifecycleCommandOwner
import com.alaeri.command.android.invokeCommandWithLifecycle
import com.alaeri.command.android.visualizer.databinding.FiltersFragmentBinding
import com.alaeri.command.core.command
import com.alaeri.command.core.invoke
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

@ExperimentalCoroutinesApi
class VisualizationOptionsFragment: Fragment(), LifecycleCommandOwner, KoinComponent {

    private val mutableCommandLoggerStateFlow= MutableStateFlow<ICommandLogger?>(null)
    override val commandScope = buildLifecycleCommandRoot(mutableCommandLoggerStateFlow)
    override val lifecycleCommandContext: LifecycleCommandContext = LifecycleCommandContext(this)
    private lateinit var listFragmentBinding: FiltersFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commandRootContext = getKoin().get<ICommandLogger>()
        mutableCommandLoggerStateFlow.value = commandRootContext
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         listFragmentBinding = FiltersFragmentBinding.inflate(inflater, container, false)
         return listFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel : VisualizationOptionsFragmentViewModel by viewModel()
        listFragmentBinding.apply {
            switchInjection.setOnCheckedChangeListener { _, isChecked -> viewModel.onInjectionSwitched(isChecked) }
            switchLifecycle.setOnCheckedChangeListener { _, isChecked -> viewModel.onLifecycleSwitched(isChecked) }
        }
        viewModel.currentOptionsLiveData.observe(viewLifecycleOwner, {
            listFragmentBinding.apply{
                switchInjection.isChecked = it.showInjection
                switchLifecycle.isChecked = it.showLifecycle
            }
        })

    }

    override fun onResume() {
        super.onResume()
        invokeCommandWithLifecycle<Unit> {
            emit(CommandState.Update("coucou"))
            val s = invoke { this@VisualizationOptionsFragment.command<String> { "hola" } }
        }
    }


}