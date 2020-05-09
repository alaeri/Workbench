package com.alaeri.cats.app.ui.cats

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.extras.viewholder.ViewHolderProvider
import androidx.recyclerview.widget.extras.viewholder.adapter.PagedListAdapterWithVHProvider
import androidx.recyclerview.widget.extras.viewholder.factory.IViewHolderFactory
import androidx.recyclerview.widget.extras.viewholder.factory.ViewHolderFactory
import com.alaeri.cats.app.cats.Cat
import com.alaeri.cats.app.databinding.CatItemBinding
import com.alaeri.command.core.Command
import com.alaeri.command.core.invoke
import com.alaeri.command.core.command
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val catsFragmentModule : Command<Module> = Any().command{  module {

        scope<CatsFragment> {
            scoped { (fragment : Fragment) -> invoke{ command<Fragment> { fragment }  } }
            factory { invoke{ command<Lifecycle>{ get<Fragment>().lifecycle } } }
            scoped<ViewModelStoreOwner> {
                invoke{
                    command<ViewModelStoreOwner> {
                        val vmStore = ViewModelStore()
                        ViewModelStoreOwner { vmStore }
                    }
                }
            }
            factory<Factory> {
                invoke {
                    command<Factory>{
                        object: Factory{
                            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                                return CatViewModel(get()) as T
                            }
                        }
                    }
                }
            }
            factory<IViewHolderFactory<Cat, CatItemVH>> {
                invoke{
                    command<IViewHolderFactory<Cat, CatItemVH>> {
                        ViewHolderFactory.newInstance<Cat, CatItemVH, CatItemBinding>(CatItemBinding::inflate){
                                binding, _ -> CatItemVH.CatVH(binding, get(), get())
                        }
                    }
                }
            }
            factory {
                invoke{
                    command<ViewHolderProvider<Cat, CatItemVH>> {
                        ViewHolderProvider(
                            getAll()
                        )
                    }
                }

            }
            factory {
                invoke{
                    command<PagedListUseCase> {  PagedListUseCase(get(), get()) }
                }
            }
            factory {
                invoke{
                    command<RefreshUseCase> { RefreshUseCase(get(), get()) }
                }
            }
            factory{
                invoke{
                    command<PagedListAdapterWithVHProvider<Cat,CatItemVH>> {
                        val diffCallback = CatDiffCallback()
                        val asyncDifferConfig: AsyncDifferConfig<Cat> = AsyncDifferConfig.Builder<Cat>(diffCallback).build()
                        PagedListAdapterWithVHProvider<Cat, CatItemVH>(get(), asyncDifferConfig)
                        //            CatsAdapter(get()).toLifecycleAdapter()
//            val pagedListAdapter = PagedListAdapter<Cat, CatItemVH>(asyncDifferConfig)
                    }
                }
            }
            viewModel{ invoke{ command<CatsViewModel>{ CatsViewModel(get(), get(), get()) } } }
        }
    }
}