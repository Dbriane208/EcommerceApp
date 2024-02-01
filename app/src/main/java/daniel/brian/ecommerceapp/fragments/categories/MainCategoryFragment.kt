package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.adapters.SpecialProductsAdapter
import daniel.brian.ecommerceapp.databinding.FragmentMainCategoryBinding
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.MainCategoryViewModel
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment : Fragment(){
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
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

        lifecycleScope.launchWhenStarted {
          viewModel.specialProduct.collectLatest {
              when(it){
                  is ResourceWrapper.Loading ->{
                      showLoading()
                  }
                  is ResourceWrapper.Success ->{
                      specialProductsAdapter.differ.submitList(it.data)
                      hideLoading()
                  }
                  is ResourceWrapper.Error ->{
                      hideLoading()
                      Log.e(TAG,it.message.toString())
                      Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                  }
                  else -> Unit
              }
          }
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
