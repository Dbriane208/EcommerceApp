package daniel.brian.ecommerceapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import daniel.brian.ecommerceapp.databinding.ProductRvItemBinding

class OfferAdapter: RecyclerView.Adapter<OfferAdapter.OfferAdapterViewHolder>() {

    // Todo - Change the ProductRVBinding
    inner class OfferAdapterViewHolder(private val binding: ProductRvItemBinding):RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OfferAdapter.OfferAdapterViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: OfferAdapter.OfferAdapterViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}