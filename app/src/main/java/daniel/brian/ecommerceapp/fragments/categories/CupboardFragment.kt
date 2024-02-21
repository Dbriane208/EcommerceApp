package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.data.Category
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.viewmodel.BaseCategoryViewModelFactory
import daniel.brian.ecommerceapp.viewmodel.CategoryViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CupboardFragment : BaseCategoryFragment(){
    @Inject
    lateinit var  firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Cupboard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it){
                    is ResourceWrapper.Loading -> {
                        showOfferLoading()
                    }
                    is ResourceWrapper.Success -> {
                        // we're able to access this because we initialized the adapters using the BestProductAdapter
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is ResourceWrapper.Error ->{
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideOfferLoading()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is ResourceWrapper.Loading -> {
                        showBestProductLoading()
                    }
                    is ResourceWrapper.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductLoading()
                    }
                    is ResourceWrapper.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideBestProductLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductsPagingRequest(){

    }

    override fun onOfferPagingRequest() {

    }
}
