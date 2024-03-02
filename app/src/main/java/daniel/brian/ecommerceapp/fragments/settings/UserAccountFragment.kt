package daniel.brian.ecommerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.data.User
import daniel.brian.ecommerceapp.databinding.FragmentUserAccountBinding
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.UserAccountViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment: Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // collecting the states
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is ResourceWrapper.Loading ->{
                        showUserLoading()
                    }
                    is ResourceWrapper.Success ->{
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is ResourceWrapper.Error ->{
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showUserInformation(data: User) {
        binding.apply {

        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    // hiding the views except the progress bar
    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}