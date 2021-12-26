package com.alaeri.cats.app.ui.cats

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.AsyncDifferConfig
import com.alaeri.cats.app.cats.Cat
import com.alaeri.cats.app.databinding.CatItemBinding
import com.alaeri.log.glide.FlowImageLoader
import com.alaeri.recyclerview.extras.viewholder.ViewHolderProvider
import com.alaeri.recyclerview.extras.viewholder.factory.IViewHolderFactory
import com.alaeri.recyclerview.extras.viewholder.factory.ViewHolderFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
object CatsFragmentModule
val catsFragmentModule = module {
    scope<CatsFragment> {
        scoped { (fragment : CatsFragment) ->  fragment  }
        factory { get<Fragment>().lifecycle }
        factory <ViewModelStore> {
            get<Fragment>().viewModelStore
        }
        factory<Factory> {
            object: Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CatItemViewModel(get()) as T
                }
            }
        }
        factory<IViewHolderFactory<Cat, CatItemVH> > {
            ViewHolderFactory.newInstance(CatItemBinding::inflate){
                    binding, _ -> CatItemVH.CatVH(binding, get(), get(), get())
            }
        }
        factory {
            val viewHolderFactories = getAll<IViewHolderFactory<Cat, CatItemVH>>()
            ViewHolderProvider(
                viewHolderFactories
            )

        }
        factory {
            PagedListUseCase(get(), get())
        }
        factory {
            RefreshUseCase(get(), get())
        }
//        factory<PagedListAdapterWithVHProvider<Cat, CatItemVH>>{
//            val diffCallback = CatDiffCallback()
//            val asyncDifferConfig: AsyncDifferConfig<Cat> = AsyncDifferConfig.Builder<Cat>(diffCallback).build()
//            PagedListAdapterWithVHProvider<Cat, CatItemVH>(get(), asyncDifferConfig)
//        }
        scoped {
            val diffCallback = CatDiffCallback()
            val asyncDifferConfig: AsyncDifferConfig<Cat> = AsyncDifferConfig.Builder<Cat>(diffCallback).build()
            Log.d("COMMAND","CREATING CATS ADAPTER")
            val viewHolderProvider : ViewHolderProvider<Cat, CatItemVH> = get()
            Log.d("COMMAND","CREATING CATS ADAPTER VHP")
            CatsAdapter(viewHolderProvider, diffCallback, asyncDifferConfig )
        }
        viewModel { CatsViewModel(get(), get()) } }

}

