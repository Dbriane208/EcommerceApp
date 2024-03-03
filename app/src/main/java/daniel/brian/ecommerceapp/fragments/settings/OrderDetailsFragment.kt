package daniel.brian.ecommerceapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import daniel.brian.ecommerceapp.adapters.BillingProductsAdapter
import daniel.brian.ecommerceapp.data.OrderStatus
import daniel.brian.ecommerceapp.data.getOrderStatus
import daniel.brian.ecommerceapp.databinding.FragmentOrderDetailBinding
import daniel.brian.ecommerceapp.helper.VerticalItemDecoration

class OrderDetailsFragment: Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setting up the adapter
        setupOrderRV()

        // getting the order form the order fragment
        val order = args.order
        binding.apply {
            "Order #${order.orderId}".also { tvOrderId.text = it }

            // implementing the step views
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            val currentOrderState = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            // setting the steps
            stepView.go(currentOrderState,false)
            if(currentOrderState == 3){
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            "${order.address.street} ${order.address.city}".also { tvAddress.text = it }
            tvPhoneNumber.text = order.address.phone
            "$${order.totalPrice}".also { tvTotalPrice.text }
        }

        // updating the products list in our adapter
        billingProductsAdapter.differ.submitList(order.products)

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupOrderRV() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}