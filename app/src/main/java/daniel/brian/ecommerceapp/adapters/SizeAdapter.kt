package daniel.brian.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import daniel.brian.ecommerceapp.databinding.SizeRvItemBinding

private var selectedPosition = -1
var onItemSizeClick: ((String) -> Unit) ?= null
class SizeAdapter : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {
    inner class SizeViewHolder(private val binding: SizeRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(size: String,position: Int){
            binding.tvSize.text = size
            if(position == selectedPosition){
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                }
            }else{
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                }
            }
        }
    }

    private  var diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeAdapter.SizeViewHolder {
        return SizeViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: SizeAdapter.SizeViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size,position)
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemSizeClick?.invoke(size)
        }

    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

}