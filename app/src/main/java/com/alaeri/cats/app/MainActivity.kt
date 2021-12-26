package com.alaeri.cats.app

import android.os.Bundle
import com.alaeri.cats.app.ui.login.LoginViewModel
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.core.context.EmptyTag
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ScopeActivity() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
        val env = LogConfig.logEnvironmentFactory.blockingLogEnvironment(EmptyTag(), collector)
        env.prepare()
        setContentView(R.layout.main_activity)
    }

    override fun onResume() {
        super.onResume()
        loginViewModel.onLogoutClicked()
    }
}
