@file:Suppress("DEPRECATION")

package daniel.brian.ecommerceapp.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.activities.ShoppingActivity
import daniel.brian.ecommerceapp.databinding.FragmentIntroductionBinding
import daniel.brian.ecommerceapp.viewmodel.IntroductionViewModel
import daniel.brian.ecommerceapp.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import daniel.brian.ecommerceapp.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import kotlinx.coroutines.flow.collect
@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigating to the accountOptionsFragment
        binding.btnStart.setOnClickListener {
            // we use button click whenever the user clicks start
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect{
                when(it){
                    // navigating to the shopping activities
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(),ShoppingActivity::class.java).also{intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    // navigating to the options fragment
                    ACCOUNT_OPTIONS_FRAGMENT ->{
                        findNavController().navigate(it)
                    }
                    else -> Unit
                }
            }
        }
    }
}
