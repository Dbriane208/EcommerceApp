package daniel.brian.ecommerceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment : Fragment(R.layout.fragment_account_options) {
    private lateinit var binding: FragmentAccountOptionsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Navigating to both the RegisterFragment and the LoginFragment
         binding.apply {
             btnRegister.setOnClickListener {
                 findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
             }

             btnLogin.setOnClickListener {
                 findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
             }
         }
    }
}
