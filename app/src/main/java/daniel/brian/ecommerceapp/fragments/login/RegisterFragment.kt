package daniel.brian.ecommerceapp.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.data.User
import daniel.brian.ecommerceapp.databinding.FragmentRegisterBinding
import daniel.brian.ecommerceapp.util.RegisterValidation
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Navigating to the LoginFragment
        binding.askLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    // trimming to remove extra spaces
                    firstName.text.toString().trim(),
                    lastName.text.toString().trim(),
                    registerEmail.text.toString().trim(),
                )
                val password = registerPass.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        // the reason we're using - collect - is because it is pulling the data from Flow in our viewModel
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is ResourceWrapper.Error -> {
                        Log.e("RegisterFragment", it.message.toString())
                    }

                    is ResourceWrapper.Loading -> {
                        binding.btnRegister.startAnimation()
                    }

                    is ResourceWrapper.Success -> {
                        Log.d("test", it.data.toString())
                        binding.btnRegister.revertAnimation()
                        Snackbar.make(requireView(),"Registration Successful! Login now",Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.registerEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.registerPass.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}
