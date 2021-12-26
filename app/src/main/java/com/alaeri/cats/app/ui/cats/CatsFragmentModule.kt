package com.alaeri.cats.app.ui.cats

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
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
import org.koin.dsl.scoped

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val catsFragmentModule = module {
    scope<CatsFragment> {

        scoped<Lifecycle> { params ->
           params.get()
        }
        viewModel { CatsViewModel(get(), get()) }



        scoped<Factory> {
            object: Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CatItemViewModel(get()) as T
                }
            }
        }
        scoped<IViewHolderFactory<Cat, CatItemVH> > {
            ViewHolderFactory.newInstance(CatItemBinding::inflate){
                    binding, _ ->
                Log.d("KOIN", "GET LIFECYCLE")
                val lifecycle : Lifecycle = get()
                Log.d("KOIN", "CREATE CATITEMVH NEXT LINE")
                CatItemVH.CatVH(binding, get(), lifecycle)
            }
        }
        scoped {
            val viewHolderFactories = getAll<IViewHolderFactory<Cat, CatItemVH>>()
            ViewHolderProvider<Cat, CatItemVH>(
                viewHolderFactories
            )

        }
        scoped {
            PagedListUseCase(get(), get())
        }
        scoped {
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
            Log.d("KOIN","CREATING CATS ADAPTER")
            val viewHolderProvider : ViewHolderProvider<Cat, CatItemVH> = get()
            Log.d("KOIN","CREATING CATS ADAPTER VHP")
            CatsAdapter(viewHolderProvider, diffCallback, asyncDifferConfig )
        }
    }




}

