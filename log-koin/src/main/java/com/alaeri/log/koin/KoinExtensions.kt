package com.alaeri.log.koin

/**
 * Created by Emmanuel Requier on 11/05/2021.
 */

import androidx.lifecycle.ViewModel
import com.alaeri.log.core.LogConfig
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Definitions
import org.koin.core.definition.Options
import org.koin.core.module.Module
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

inline fun Any.module(crossinline body: Module.()->Unit): Module{
    return LogConfig.logBlocking(){
        org.koin.dsl.module {
            LogConfig.logBlocking(){
                body.invoke(this)
            }
        }
    }
}
inline fun <reified T> Module.single(crossinline body: Scope.() -> T): BeanDefinition<T>{
    return LogConfig.logBlocking {
        single {
            LogConfig.logBlocking {
                body.invoke(this)
            }
        }
    }
}
//crossinline body: Scope.(parameters: DefinitionParameters) -> T
inline fun <reified T> Module.scope(scopeSet: ScopeDSL.()->Unit): Unit{
    return LogConfig.logBlocking {
        scope<T> {
            LogConfig.logBlocking {
                scopeSet.invoke(this)
            }
        }
    }
}

inline fun <reified T> ScopeDSL.scoped(qualifier: Qualifier? = null,
                                       override: Boolean = false,
                                       noinline definition: Scope.(DefinitionParameters) -> T): BeanDefinition<T>{

    val loggedDefinition : Definition<T> = { p -> definition.invoke(this, p)}
    return LogConfig.logBlocking {
        return Definitions.saveSingle(
            qualifier,
            loggedDefinition,
            scopeDefinition,
            Options(isCreatedAtStart = false, override = override)
        )
    }
}

inline fun <reified T> Module.factory(crossinline body: Scope.() -> T): BeanDefinition<T>{
    return LogConfig.logBlocking {
        factory {
            LogConfig.logBlocking {
                body.invoke(this)
            }
        }
    }
}
inline fun <reified T: ViewModel> Module.viewModel(crossinline body: Scope.() -> T): BeanDefinition<T>{
    return LogConfig.logBlocking {
        viewModel {
            LogConfig.logBlocking {
                body.invoke(this)
            }
        }
    }
}
inline fun <reified T> Scope.get(qualifier: Qualifier? = null,
                                 noinline parameters: ParametersDefinition? = null): T{
    return LogConfig.logBlocking {
        get(qualifier, parameters)
    }
}