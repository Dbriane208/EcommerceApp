package daniel.brian.ecommerceapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.data.Order
import daniel.brian.ecommerceapp.data.OrderStatus
import daniel.brian.ecommerceapp.data.getOrderStatus
import daniel.brian.ecommerceapp.databinding.OrderItemBinding
@Suppress("DEPRECATION")
class AllOrdersAdapter: Adapter<AllOrdersAdapter.AllOrdersViewHolder>() {

    inner class AllOrdersViewHolder(private val binding: OrderItemBinding): ViewHolder(binding.root){
        fun bind(allOrders: Order) {
            binding.apply {
                tvOrderId.text = allOrders.orderId.toString()
                tvOrderDate.text = allOrders.date
                val resources = itemView.resources
                val colorDrawable = when(getOrderStatus(allOrders.orderStatus)){
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatus.Confirmed -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Delivered -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Shipped -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Canceled -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                    is OrderStatus.Returned -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                }
                imageOrderState.setImageDrawable(colorDrawable)
            }
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllOrdersAdapter.AllOrdersViewHolder {
        return AllOrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    val diffUtil = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
           return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onBindViewHolder(holder: AllOrdersAdapter.AllOrdersViewHolder, position: Int) {
        val allOrders = differ.currentList[position]
        holder.bind(allOrders)
        holder.itemView.setOnClickListener {
            onAllOrdersClick?.invoke(allOrders)
        }

    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    var onAllOrdersClick: ((Order) -> Unit) ?= null
}