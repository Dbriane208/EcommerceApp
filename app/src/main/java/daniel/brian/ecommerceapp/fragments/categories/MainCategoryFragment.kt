@file:Suppress("DEPRECATION")

package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.adapters.BestDealsAdapter
import daniel.brian.ecommerceapp.adapters.BestProductsAdapter
import daniel.brian.ecommerceapp.adapters.SpecialProductsAdapter
import daniel.brian.ecommerceapp.databinding.FragmentMainCategoryBinding
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.MainCategoryViewModel
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment : Fragment(){
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductsRV()
        setUpBestDealsRV()
        setUpBestProductsRV()

        lifecycleScope.launchWhenStarted {
          viewModel.specialProduct.collectLatest {
              when(it){
                  is ResourceWrapper.Loading ->{
                      showLoading()
                      binding.specialProductsProgressBar.visibility = View.VISIBLE
                  }
                  is ResourceWrapper.Success ->{
                      specialProductsAdapter.differ.submitList(it.data)
                      hideLoading()
                      binding.specialProductsProgressBar.visibility = View.GONE
                  }
                  is ResourceWrapper.Error ->{
                      hideLoading()
                      binding.specialProductsProgressBar.visibility = View.GONE
                      Log.e(TAG,it.message.toString())
                      Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                  }
                  else -> Unit
              }
          }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDeals.collectLatest {
                when(it){
                    is ResourceWrapper.Loading ->{
                        showLoading()
                        binding.bestDealsProgressBar.visibility = View.VISIBLE
                    }
                    is ResourceWrapper.Success ->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                        binding.bestDealsProgressBar.visibility = View.GONE
                    }
                    is ResourceWrapper.Error ->{
                        hideLoading()
                        binding.bestDealsProgressBar.visibility = View.GONE
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is ResourceWrapper.Loading -> {
                        showLoading()
                        binding.bestProductsProgressBar.visibility = View.VISIBLE
                    }
                    is ResourceWrapper.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                        binding.bestProductsProgressBar.visibility = View.GONE
                    }
                    is ResourceWrapper.Error ->{
                        binding.bestProductsProgressBar.visibility = View.GONE
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // detecting we're at the end of the nested scroll view
        binding.nestedScrollMainCategory.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener{v,_,scrollY,_,_ ->
                if (v.getChildAt(0).bottom <= v.height + scrollY){
                    viewModel.fetchBestProducts()
                }
            }
        )

    }

    private fun setUpBestProductsRV() {
        bestProductsAdapter = BestProductsAdapter()
        binding.bestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),3,LinearLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpBestDealsRV() {
       bestDealsAdapter = BestDealsAdapter()
       binding.bestDeals.apply {
           layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
           adapter = bestDealsAdapter
       }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun setUpSpecialProductsRV() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.specialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsAdapter
        }
    }
}
