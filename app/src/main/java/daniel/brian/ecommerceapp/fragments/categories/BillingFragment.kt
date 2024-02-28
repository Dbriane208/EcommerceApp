package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.adapters.AddressAdapter
import daniel.brian.ecommerceapp.adapters.BillingProductsAdapter
import daniel.brian.ecommerceapp.data.CartProduct
import daniel.brian.ecommerceapp.databinding.FragmentBillingBinding
import daniel.brian.ecommerceapp.helper.HorizontalItemDecoration
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.BillingViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class BillingFragment: Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val vieModel by viewModels<BillingViewModel>()
    // we want to receive the price and products from the cartFragment
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting up the recyclerView
        setupBillingProductsRV()
        setupAddressRV()

        // add address
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // observing and collecting our state
        lifecycleScope.launchWhenStarted {
            vieModel.address.collectLatest {
                when(it){
                    is ResourceWrapper.Loading ->{
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is ResourceWrapper.Success ->{
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is ResourceWrapper.Error ->{
                        binding.progressbarAddress.visibility = View.GONE
                        Snackbar.make(requireView(),"Error ${it.message}",Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(products)
        "$ $totalPrice".also { binding.tvTotalPrice.text = it }
    }

    private fun setupAddressRV() {
       binding.rvAddress.apply {
           layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
           adapter = addressAdapter
           addItemDecoration(HorizontalItemDecoration())
       }
    }

    private fun setupBillingProductsRV() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}