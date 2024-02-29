package daniel.brian.ecommerceapp.fragments.categories

import android.app.AlertDialog
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
import daniel.brian.ecommerceapp.data.Address
import daniel.brian.ecommerceapp.data.CartProduct
import daniel.brian.ecommerceapp.data.Order
import daniel.brian.ecommerceapp.data.OrderStatus
import daniel.brian.ecommerceapp.databinding.FragmentBillingBinding
import daniel.brian.ecommerceapp.helper.HorizontalItemDecoration
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.BillingViewModel
import daniel.brian.ecommerceapp.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class BillingFragment: Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    // we want to receive the price and products from the cartFragment
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    private var selectedAddress: Address ?= null
    private  val orderViewModel by viewModels<OrderViewModel>()

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
            billingViewModel.address.collectLatest {
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

        // we collect our state
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is ResourceWrapper.Loading ->{
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is ResourceWrapper.Success ->{
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(),"Your order was placed",Snackbar.LENGTH_SHORT).show()
                    }
                    is ResourceWrapper.Error ->{
                        binding.progressbarAddress.visibility = View.GONE
                        Snackbar.make(requireView(),"Error ${it.message}",Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(products)
        "$ $totalPrice".also { binding.tvTotalPrice.text = it }

        // we add adapter onClick
        addressAdapter.onclick = {
            selectedAddress = it
        }

        // Adding click functionality to ur button place holder
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null){
                Snackbar.make(requireView(),"Please select an Address",Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){ dialog,_ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
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