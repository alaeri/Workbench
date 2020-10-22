package com.alaeri.command.di

import android.util.Log
import androidx.lifecycle.ViewModel
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module
import org.koin.ext.scope
import kotlin.random.Random

fun KoinApplication.invokeModules(executionContext: ExecutionContext<Any?>, vararg moduleCommands : Command<Module>){
    val modules = moduleCommands.map {
        executionContext.invoke { it }
    }
    this.modules(modules)
}
fun Any.commandModule(body: KoinCommandModule.()->Unit): Command<Module> {
    return command(nomenclature = CommandNomenclature.Injection.Initialization) {
        val executionContext = this
        module {
            val koinCommandModule = KoinCommandModule(Random.nextBits(10),this, executionContext)
            koinCommandModule.body()
        }
    }
}
class KoinInvokationScope(val scopeA: Scope, val executionContext: ExecutionContext<*>){
    fun androidContext() = scopeA.androidContext()
    inline fun <reified T> get(): T = executionContext.invokeCommand(nomenclature = CommandNomenclature.Injection.Retrieval) { scopeA.get<T>() }
    inline fun <reified T: Any> getAll(): List<T> = executionContext.invokeCommand(nomenclature = CommandNomenclature.Injection.Retrieval) { scopeA.getAll<T>() }
}

class KoinCommandModule(val id:Int, val module: Module, val executionContext: ExecutionContext<Module>) {
    inline fun <reified T> commandSingle(noinline body: KoinInvokationScope.() -> T): BeanDefinition<T> {
        Log.d("COMMAND2-SINGLE", "${T::class.java}")
        val beanDefinition: BeanDefinition<T> =
            executionContext.invokeCommand(nomenclature = CommandNomenclature.Injection.Initialization) {
                Log.d("COMMAND2-SINGLE", "${T::class.java}")
                module.single<T> {
                    invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
                        Log.d("COMMAND2-SINGLE", "${T::class.java}")
                        KoinInvokationScope(this@single, this).body()
                    }
                }
            }
        Log.d("COMMAND2", "$beanDefinition")
        return beanDefinition
    }

    inline fun <reified T> commandScope(noinline body: CommandScopeDSL.() -> Unit) {
        Log.d("COMMAND2","${T::class.java}")
        executionContext.invokeCommand<Module, Unit> {
            Log.d("COMMAND2","${T::class.java}")
            val executionContext2 = this
            module.scope<T> {
                Log.d("COMMAND2","${T::class.java}")
                val scopeDSL = this
                val commandScopeDSL = CommandScopeDSL(this, executionContext).apply(body)
            }
        }
//        module.scope<T> {
//            val commandScopeDSL = CommandScopeDSL(this, executionContext).apply(body)
//        }
    }

    inline fun <reified T> commandFactory(noinline body: KoinInvokationScope.() -> T): BeanDefinition<T> {
        return executionContext.invokeCommand<Module, BeanDefinition<T>>(nomenclature = CommandNomenclature.Injection.Initialization) {
            val beanExecutionContext = this
            module.factory<T> {
                Log.d("COMMAND2-FACTORY", "${T::class.java}")
                val scope = this
                beanExecutionContext.invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
                    val typedExecutionContext: ExecutionContext<T> = this
                    Log.d("COMMAND2-FACTORY", "${T::class.java}")
                    KoinInvokationScope(scope, typedExecutionContext).body()
                }
            }
        }
    }

    inline fun <reified T : ViewModel> commandViewModel(noinline body: KoinInvokationScope.() -> T): BeanDefinition<T> {
        //TODO reenable this...
//        Log.d("COMMAND2-VIEWMODEL1","${T::class.java}")
//        return executionContext.invokeCommand<Module, BeanDefinition<T>>(nomenclature = CommandNomenclature.Injection.Initialization){
//            val innerContext : ExecutionContext<BeanDefinition<T>> = this@invokeCommand
//            Log.d("COMMAND2-VIEWMODEL2","${T::class.java}")
//            val clazz = T::class
//            val beanDefinition : BeanDefinition<T> = module.viewModel() {
//                Log.d("COMMAND2-VIEWMODEL3","${T::class.java} $clazz")
//                val scope = this
//                innerContext.invokeCommand<BeanDefinition<T>,T>(nomenclature = CommandNomenclature.Injection.Creation) {
//                    Log.d("COMMAND2-VIEWMODEL4","${T::class.java} $clazz")
//                    KoinInvokationScope<T>(scope, this).body()
//                }
//            }
//            Log.d("COMMAND2-VIEWMODEL2.5","$beanDefinition --- ${T::class.java}")
//            beanDefinition
//        }
        return module.viewModel {
            Log.d("COMMAND2-VIEWMODEL1","$body ${T::class.java}")
            val scope = this
            KoinInvokationScope(scope, executionContext).body()
        }

    }
}

class CommandScopeDSL(val scopeDSL: ScopeDSL, val parentExecutionContext: ExecutionContext<Module>) {

    inline fun <reified T> scoped(noinline body: KoinInvokationScope.(parameters: DefinitionParameters) -> T): BeanDefinition<T> {
        return parentExecutionContext.invokeCommand<Module, BeanDefinition<T>>(nomenclature = CommandNomenclature.Injection.Initialization) {
            Log.d("COMMAND2SCOPE-SCOPED", "${T::class.java}")
            val executionContext = this
            val definition: Definition<T> = { params: DefinitionParameters ->
                executionContext.invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
                    Log.d("COMMAND2SCOPE-SCOPED", "${T::class.java}")
                    val executionContextInside: ExecutionContext<T> = this
                    val invokationScope = KoinInvokationScope(scope, executionContextInside)
                    invokationScope.body(params)
                }
            }
            scopeDSL.scoped<T>(null, false, definition)
        }
    }

    inline fun <reified T> factory(noinline body: KoinInvokationScope.() -> T): BeanDefinition<T> {
        Log.d("COMMAND2SCOPE-FACTORY", "${T::class.java}")
        return parentExecutionContext.invokeCommand<Module, BeanDefinition<T>>(nomenclature = CommandNomenclature.Injection.Initialization) {
            val executionContext = this
//            scopeDSL.factory<T> {
//                Log.d("COMMAND2SCOPE-FACTORY", "${T::class.java}")
//                val factoryScope = this
//                executionContext.invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
//                    val executionContextInside: ExecutionContext<T> = this
//                    KoinInvokationScope(factoryScope, executionContextInside).body()
//                }
//            }
            val definition : Definition<T> = {
                val factoryScope = this
                val obj : T = executionContext.invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
                    val executionContextInside: ExecutionContext<T> = this
                    KoinInvokationScope(factoryScope, executionContextInside).body()
                }
                Log.d("COMMAND2SCOPE-FACTORY", "return $obj")
                obj
            }
            scopeDSL.factory<T>(null, false, definition = definition)
        }
    }

    inline fun <reified T : ViewModel> viewmodel(noinline body: KoinInvokationScope.() -> T): BeanDefinition<T> {
        Log.d("COMMAND2SCOPE-VIEWMODEL", "${T::class.java}")
        return parentExecutionContext.invokeCommand<Module, BeanDefinition<T>>(nomenclature = CommandNomenclature.Injection.Initialization) {
            val scopeExecutionContext = this
            val definition : Definition<T> = {
                val factoryScope = this
                val viewModel : T = scopeExecutionContext.invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
                    val executionContextInside: ExecutionContext<T> = this
                    KoinInvokationScope(factoryScope, executionContextInside).body()
                }
                Log.d("COMMAND2SCOPE-VIEWMODEL", "return $viewModel")
                viewModel
            }
//            scopeDSL.viewModel<T> {
//                val vmScope = this
//                scopeExecutionContext.invokeCommand<BeanDefinition<T>, T>(nomenclature = CommandNomenclature.Injection.Creation) {
//                    val childExecutionContext : ExecutionContext<T> = this
//                    Log.d("COMMAND2SCOPE-VIEWMODEL", "${T::class.java}")
//                    KoinInvokationScope(vmScope, childExecutionContext).body()
//                }
//            }
            scopeDSL.viewModel<T>(null, false, definition)
        }
    }
}