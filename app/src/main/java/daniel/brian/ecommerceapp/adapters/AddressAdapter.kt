package daniel.brian.ecommerceapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.data.Address
import daniel.brian.ecommerceapp.databinding.AddressRvItemBinding

@Suppress("DEPRECATION")
class AddressAdapter : Adapter<AddressAdapter.AddressViewHolder>() {
    inner class AddressViewHolder(val binding: AddressRvItemBinding) : ViewHolder(binding.root){
        fun bind(address: Address, isSelected: Boolean){
            binding.apply {
                buttonAddress.text = address.addressTitle
                // changing the background color of the button
                if (isSelected){
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                }else{
                   buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }
    }

    val diffUtil = object  : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle &&
                    oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    // Declaring this variable to help us in changing the background color of when the button is clicked
    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address,selectedAddress == position)
        // we have accessed the binding by allowing the the AddressViewHolder to be public
        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onclick?.invoke(address)
        }
    }
    // to prevent selecting two addresses when we want to add and address
    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    var onclick: ((Address) -> Unit) ?= null
}