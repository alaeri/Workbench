package com.alaeri.cats.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.alaeri.cats.app.ui.cats.CatsFragment
import com.alaeri.cats.app.ui.login.LoginFragment
import com.alaeri.cats.app.ui.login.LoginState
import com.alaeri.cats.app.ui.login.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, LoginFragment.newInstance())
//                    .commitNow()
//        }
        loginViewModel.currentState.observe(this, Observer {
            when(it){
                is LoginState.LoggedIn -> {
                    Log.d("NAV","cats")
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.catsFragment)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.graphFragment)
                }
                is LoginState.LoggedOut -> {
                    Log.d("NAV","login")
                    findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loginViewModel.onLogoutClicked()
    }
}
