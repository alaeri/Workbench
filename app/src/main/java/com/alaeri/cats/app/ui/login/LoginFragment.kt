package com.alaeri.cats.app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alaeri.cats.app.databinding.LoginFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel.currentState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginState.LoggedOut -> {
                    binding.apply {
                        progressCircular.hide()
                        message.text = it.loginError?.message ?: "Veuillez vous connecter"
                        loginButton.visibility = View.VISIBLE
                        loginButton.text = "Se connecter"
                        loginButton.setOnClickListener { loginViewModel.onSubmitClicked("Bernadette") }
                    }
                }
                is LoginState.Loading -> {
                    binding.apply {
                        message.text = "Connexion en cours"
                        loginButton.visibility = View.INVISIBLE
                        progressCircular.show()
                    }
                }
                is LoginState.LoggedIn -> {
                    binding.apply {
                        message.text =
                            "Bienvenue ${it.user.firstName}. Vous avez ${it.user.favoritesCount} favoris"
                        loginButton.visibility = View.VISIBLE
                        loginButton.text = "Se deconnecter"
                        loginButton.setOnClickListener { loginViewModel.onLogoutClicked() }
                        progressCircular.hide()
                    }
                }
            }
        })
    }
}
