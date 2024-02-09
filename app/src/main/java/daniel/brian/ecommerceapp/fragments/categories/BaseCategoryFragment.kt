package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.adapters.BestProductsAdapter
import daniel.brian.ecommerceapp.adapters.OfferAdapter
import daniel.brian.ecommerceapp.databinding.FragmentBaseCategoryBinding

class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    private lateinit var offerAdapter: OfferAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBaseCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBestProductsRV()
    }

    private fun setupBestProductsRV() {
        bestProductsAdapter = BestProductsAdapter()
        binding.bestProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }
}
