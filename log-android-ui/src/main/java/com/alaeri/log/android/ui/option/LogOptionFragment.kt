package com.alaeri.log.android.ui.option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.alaeri.log.android.ui.databinding.OptionsFragmentBinding
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.repository.LogRepository
import com.alaeri.log.synth.Player
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Emmanuel Requier on 23/05/2021.
 */
sealed class LogAppearance{
//    data class Parameter(): LogAppearance()
//    data class Tag(): LogAppearance()
//    data class Return(): LogAppearance()

}
data class LogEntity(val identity: IdentityRepresentation,
                     val description: String)
data class LogOption(val parameter: LogEntity, val enabled: Boolean)
class LogOptionsRepository(private val logRepository: LogRepository){
    private val map: Map<IdentityRepresentation, LogOption> = mutableMapOf()
    //val availableOptions: Flow<List<LogOption>> = combine(logRepository.listAsFlow)
    fun saveOptions(updatedOptions: List<LogOption>){

    }
}
class LogOptionViewModel(): ViewModel(){

}
class LogOptionFragment : Fragment(){

    private lateinit var binding: OptionsFragmentBinding

    val logOptionViewModel: LogOptionViewModel by viewModel<LogOptionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OptionsFragmentBinding.inflate(inflater)
        binding.playButton.setOnClickListener {
            val player = Player()
            player.play(20000.0)
        }
        return binding.root
    }
}