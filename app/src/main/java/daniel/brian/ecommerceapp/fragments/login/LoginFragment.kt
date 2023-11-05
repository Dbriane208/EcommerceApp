package daniel.brian.ecommerceapp.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.activities.ShoppingActivity
import daniel.brian.ecommerceapp.databinding.FragmentLoginBinding
import daniel.brian.ecommerceapp.dialog.setupBottomSheet
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.LoginViewModel

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigating to the RegisterFragment
        binding.txtLoginInfo.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Getting the usersInput and passing them to the viewModel
        binding.apply {
            btnLogin.setOnClickListener {
                val email = txtEmail.text.toString().trim()
                val password = txtPwd.text.toString()
                // passing the received input to the viewModel for authentication
                viewModel.login(email, password)
            }
        }

        // Implementing the forgot password listener
        binding.txtForgotPwd.setOnClickListener {
            setupBottomSheet { email ->
                // This defines what to do when the onSendClick is clicked
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is ResourceWrapper.Error -> {
                        Snackbar.make(requireView(), "Error : ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is ResourceWrapper.Loading -> {
                    }

                    is ResourceWrapper.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Reset link sent to your email",
                            Snackbar.LENGTH_LONG,
                        ).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is ResourceWrapper.Error -> {
                        // The it message is extracted from the exception it.message.toString when the callback fails in the viewModel
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.btnLogin.revertAnimation()
                    }

                    is ResourceWrapper.Loading -> {
                        binding.btnLogin.startAnimation()
                    }

                    is ResourceWrapper.Success -> {
                        // It will be triggered when the details given are authenticated
                        binding.btnLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}
