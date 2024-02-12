package daniel.brian.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import daniel.brian.ecommerceapp.databinding.ViewpagerImageItemBinding

class ViewPager2ImageAdapter : RecyclerView.Adapter<ViewPager2ImageAdapter.ViewPager2ImageViewHolder>() {
    inner class ViewPager2ImageViewHolder(private val binding: ViewpagerImageItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(imagePath: String){
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    private  val diffUtil = object  : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPager2ImageAdapter.ViewPager2ImageViewHolder {
        return ViewPager2ImageViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewPager2ImageAdapter.ViewPager2ImageViewHolder,
        position: Int
    ) {
      val  viewPagerItem = differ.currentList[position]
      holder.bind(viewPagerItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}