package daniel.brian.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import daniel.brian.ecommerceapp.data.Product
import daniel.brian.ecommerceapp.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder (private val binding :BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeals)
                bestDealProductName.text = product.name
                ("$" + product.price.toString()).also { bestDealOldPrice.text = it  }
                bestDealOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                if(product.offerPercentage != null){
                    ("$" + (product.price - (product.price * product.offerPercentage)).toString()).also { bestDealNewPrice.text = it }
                } else{
                   ("$" + product.price.toString()).also {  bestDealNewPrice.text = it }
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestDealsAdapter.BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onBindViewHolder(holder: BestDealsAdapter.BestDealsViewHolder, position: Int) {
        val bestDeals = differ.currentList[position]
        holder.bind(bestDeals)

        holder.itemView.setOnClickListener {
            onClick?.invoke(bestDeals)
        }
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    var onClick: ((Product) -> Unit) ?= null
}