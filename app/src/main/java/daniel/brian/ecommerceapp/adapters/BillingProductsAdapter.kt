package daniel.brian.ecommerceapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import daniel.brian.ecommerceapp.data.CartProduct
import daniel.brian.ecommerceapp.databinding.BillingProductsRvItemBinding
import daniel.brian.ecommerceapp.helper.getProductPrice

class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {
    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) : ViewHolder(binding.root){
          fun bind(billingProduct: CartProduct){
              binding.apply {
                  // using Glide to show the image of the specific product
                  Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                  tvProductCartName.text = billingProduct.product.name
                  tvBillingProductQuantity.text = billingProduct.quantity.toString()

                  val priceAfterPercentage = billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                  "$ ${String.format("%.2f",priceAfterPercentage)}".also { tvProductCartPrice.text = it }

                  imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor?: Color.TRANSPARENT))
                  tvCartProductSize.text = billingProduct.selectedSize?:"".also { imageCartProductSize.setImageDrawable(
                      ColorDrawable(Color.TRANSPARENT)
                  ) }
              }
          }
    }

    val diffUtil = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return  BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }

}