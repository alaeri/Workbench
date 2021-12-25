package com.alaeri.log.android.ui.option

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.alaeri.log.android.ui.databinding.OptionsFragmentBinding
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.repository.LogRepository
import com.alaeri.log.serialize.serialize.SerializedLog
import com.alaeri.log.serialize.serialize.SerializedLogMessage
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation
import com.alaeri.log.synth.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.Executors

/**
 * Created by Emmanuel Requier on 23/05/2021.
 */
sealed class LogAppearance{
//    data class Parameter(): LogAppearance()
//    data class Tag(): LogAppearance()
//    data class Return(): LogAppearance()

}
data class DefaultSerializedEntity(val className: String): EntityRepresentation<Any>
data class LogEntity(val identity: IdentityRepresentation,
                     val description: String)
data class LogOption(val parameter: LogEntity, val enabled: Boolean)
class LogOptionsRepository(private val logRepository: LogRepository){
    private val map: Map<IdentityRepresentation, LogOption> = mutableMapOf()
    //val availableOptions: Flow<List<LogOption>> = combine(logRepository.listAsFlow)
    fun saveOptions(updatedOptions: List<LogOption>){

    }
}
class LogOptionViewModel(logRepo: LogRepository): ViewModel(){

    private var countFlow = flow<Int> {
        repeat(100000){
            emit(it)
            delay(100)
        }
    }

    data class TimeAndKey(val s: Int, val key: SerializedLog<IdentityRepresentation>)
    data class Acc(val completed: List<SerializedLog<IdentityRepresentation>>,
                   val current: MutableList<SerializedLog<IdentityRepresentation>>,
                   val index: Int,
                   var lastScannedLog: SerializedLog<IdentityRepresentation>?
    )
    val l = combine(logRepo.listAsFlow, countFlow){a, c -> TimeAndKey(c, a[a.size-1]) }.scan(Acc(
        listOf(), mutableListOf(), 0, null)){acc, tak ->  if(acc.index == tak.s){
            acc.current.add(tak.key)
            acc.lastScannedLog = tak.key
            acc
        }else{
            if(acc.lastScannedLog == tak.key){
                acc.copy(completed = acc.current, current = mutableListOf(), index = tak.s)
            }else{
                acc.copy(completed = acc.current, current = mutableListOf(tak.key), index = tak.s)
            }

        }
    }.map { it.index to it.completed }.distinctUntilChanged().map { it.second }


//    val logBatches = logRepo.mapAsFlow.map { it.filterValues { it.isActive }.keys }.
//        .sample(1000).onEach { delay(1000) }
    val logBatches = l

}
class LogOptionFragment : Fragment(){


    private var job: Job? = null
    private lateinit var binding: OptionsFragmentBinding

    val logOptionViewModel: LogOptionViewModel by viewModel<LogOptionViewModel>()
    val player = Player()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = OptionsFragmentBinding.inflate(inflater)
        binding.playButton.setOnClickListener {
            if(job != null){
                job?.cancel()
                job = null
            }else{
                job = lifecycleScope.launchWhenCreated {
                    withContext(Executors.newSingleThreadExecutor().asCoroutineDispatcher()){
                        Log.d("Player","collecting log batches: $it")
                        logOptionViewModel.logBatches.onEach { delay(100) }.collect{

                            val notes = it.map { it.tag.identity.index.toDouble()}.toSet().map {  it % 1000 }
                            val success = it.firstOrNull()?.message as? SerializedLogMessage.Success
                            val ent = success?.entityRepresentation as? DefaultSerializedEntity
                            val notes2 = it.map { it.tag as ListRepresentation }.map { it.representations.filterIsInstance<ReceiverRepresentation>().firstOrNull() }.map {
                                it?.type?.clazz?.simpleName
                            }.filterNotNull().toSet().map { when(it){
                                "CatItemViewModel" -> 440
                                "FlowImageLoader" -> 600
                                else -> it.hashCode() % 1000
                            }.toDouble() }

                            Log.d("Player","playing: ${it.size} notes: $notes message: ${notes2}")
                            player.play(notes2)
                        }
                    }
                }
            }

            //player.play(listOf(440.0))
        }
        return binding.root
    }
}