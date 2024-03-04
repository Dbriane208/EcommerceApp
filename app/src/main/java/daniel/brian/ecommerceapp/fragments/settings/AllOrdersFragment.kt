package daniel.brian.ecommerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.adapters.AllOrdersAdapter
import daniel.brian.ecommerceapp.databinding.FragmentAllOrdersBinding
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.AllOrdersViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AllOrdersFragment: Fragment() {
    private lateinit var binding: FragmentAllOrdersBinding
    private val viewModel by viewModels<AllOrdersViewModel>()
    private val allOrdersAdapter by lazy { AllOrdersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting up the recycler view
        setupAllOrdersRV()

        //collecting our state
        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when(it){
                    is ResourceWrapper.Loading ->{
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is ResourceWrapper.Success ->{
                        binding.progressbarAllOrders.visibility = View.GONE
                        allOrdersAdapter.differ.submitList(it.data)
                    }
                    is ResourceWrapper.Error ->{
                        Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        allOrdersAdapter.onAllOrdersClick = {
            val action = AllOrdersFragmentDirections.actionAllOrdersFragment2ToOrderDetailsFragment(it)
            findNavController().navigate(action)
        }

    }

    private fun setupAllOrdersRV() {
        binding.rvAllOrders.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            this.adapter = allOrdersAdapter
        }
    }
}