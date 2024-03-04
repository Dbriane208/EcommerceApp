package daniel.brian.ecommerceapp.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shuhart.stepview.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.activities.LoginActivity
import daniel.brian.ecommerceapp.databinding.FragmentProfileBinding
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // calling the on logout function
        onLogOutClick()

        // navigating to user account
        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        // navigating to orders
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment2)
        }

        // navigating to billing
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f, emptyArray(), false)
            findNavController().navigate(action)
        }

        // updating the version code
        "Version ${BuildConfig.VERSION_CODE}".also { binding.tvVersion.text = it }

        // collecting our state
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest { it ->
                @Suppress("ktlint:standard:op-spacing")
                when (it) {
                    is ResourceWrapper.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is ResourceWrapper.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(
                            ColorDrawable(
                                Color.BLACK,
                            ),
                        ).into(binding.imageUser)
                        "${it.data.firstName} ${it.data.lastName}".also { binding.tvUserName.text = it }
                    }
                    is ResourceWrapper.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onLogOutClick() {
        binding.linearLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
