package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.Product
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _specialProducts = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val specialProduct: StateFlow<ResourceWrapper<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val bestDeals: StateFlow<ResourceWrapper<List<Product>>> = _bestDeals

    private val _bestProducts = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val bestProducts: StateFlow<ResourceWrapper<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    internal data class PagingInfo(
        var bestProductsPage: Long = 1,
        // used to compare the current list with the old list - if they're equal we want to
        // end the paging
        var oldBestProducts: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchBestProducts() {
        // used to determine if we can page or not
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(ResourceWrapper.Loading())
            }
            // Todo - change the value in whereEqualTo
            firestore.collection("products")
                // using limit to only load 10 items at a time dynamically
                .limit(pagingInfo.bestProductsPage * 10).get()
                .addOnSuccessListener { result ->
                    val bestProducts = result.toObjects(Product::class.java)
                    // checking whether the best products are equal to the old products - if it's the case we stop the paging
                    pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts
                    // updating the old products items
                    pagingInfo.oldBestProducts = bestProducts
                    viewModelScope.launch {
                        _bestProducts.emit(ResourceWrapper.Success(bestProducts))
                    }
                    // incrementing the page triggering to load more
                    pagingInfo.bestProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(ResourceWrapper.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDeals.emit(ResourceWrapper.Loading())
        }

        firestore.collection("products")
            .whereEqualTo("category","Chairs").get().addOnSuccessListener {result ->
                val bestDeals = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDeals.emit(ResourceWrapper.Success(bestDeals))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }

    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(ResourceWrapper.Loading())
        }

        firestore.collection("products")
            .whereEqualTo("category","Special products").get().addOnSuccessListener {result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(ResourceWrapper.Success(specialProductsList))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _specialProducts.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }

}