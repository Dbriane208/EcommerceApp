package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.adapters.BestProductsAdapter
import daniel.brian.ecommerceapp.databinding.FragmentBaseCategoryBinding
import daniel.brian.ecommerceapp.util.showBottomNavigationView

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

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
        setupOfferProductsRV()

        // invoking the click listener of the offer and best products
        offerAdapter.onClick = {
            val b = Bundle().apply { putParcelable("products",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("products",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // scrolling will be horizontal
                if (!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener{
                v, _, scrollY, _, _ ->
                if (v.getChildAt(0).bottom <= v.height + scrollY){
                    onBestProductsPagingRequest()
                }
            }
        )
    }

    // making the functions open so that they can be overrideable
    open fun onBestProductsPagingRequest() {
        TODO("Not yet implemented")
    }

    open  fun onOfferPagingRequest() {

    }

    fun showOfferLoading(){
        binding.offerProgressBar.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.offerProgressBar.visibility = View.GONE
    }

    fun showBestProductLoading(){
        binding.bestPProgressBar.visibility = View.VISIBLE
    }

    fun hideBestProductLoading(){
        binding.bestPProgressBar.visibility = View.GONE
    }
    private fun setupOfferProductsRV() {
        binding.rvOffer.apply {
            layoutManager = GridLayoutManager(requireContext(),3,LinearLayoutManager.VERTICAL,false)
            adapter = offerAdapter
        }
    }

    private fun setupBestProductsRV() {
        binding.bestProductsRV.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}
