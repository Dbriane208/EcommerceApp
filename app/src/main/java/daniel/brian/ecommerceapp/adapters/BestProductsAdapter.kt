package daniel.brian.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import daniel.brian.ecommerceapp.data.Product
import daniel.brian.ecommerceapp.databinding.ProductRvItemBinding

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(bestProductImage)
                bestProductName.text = product.name
                bestProductPrice.text = product.price.toString()
                if(product.offerPercentage != null){
                    bestDealNewPrice.text = (product.price -(product.price * product.offerPercentage)).toString()
                }else{
                    bestProductPrice.text = product.price.toString()
                }
            }

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestProductsAdapter.BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    private val diffUtil = object  : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onBindViewHolder(
        holder: BestProductsAdapter.BestProductsViewHolder,
        position: Int
    ) {
        val bestProducts = differ.currentList[position]
         holder.bind(bestProducts)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}