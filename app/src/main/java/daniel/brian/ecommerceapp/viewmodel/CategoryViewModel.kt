package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import daniel.brian.ecommerceapp.data.Category
import daniel.brian.ecommerceapp.data.Product
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// in this class we cannot use dagger hilt because it cannot allow us
// to pass the category inside the constructor so we'll create a viewModelFactory
class CategoryViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel(){

    private val _offerProducts = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    val pagingInfo = MainCategoryViewModel.PagingInfo()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    private fun fetchBestProducts() {
        if(!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(ResourceWrapper.Loading())
            }

            firestore.collection("products").whereEqualTo("category", category.category)
                .limit(pagingInfo.newItemsPage * 10)
                .whereEqualTo("offerPercentage", null).get()
                .addOnSuccessListener {
                    val productsBest = it.toObjects(Product::class.java)
                    // checking whether the product best are equal to the old products
                    pagingInfo.isPagingEnd = productsBest == pagingInfo.oldItems
                    // updating the old products items
                    pagingInfo.oldItems = productsBest
                    viewModelScope.launch {
                        _bestProducts.emit(ResourceWrapper.Success(productsBest))
                    }
                    pagingInfo.newItemsPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(ResourceWrapper.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun fetchOfferProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _offerProducts.emit(ResourceWrapper.Loading())
            }

            firestore.collection("products").whereEqualTo("category", category.category)
                .limit(pagingInfo.newItemsPage * 10)
                .whereNotEqualTo("offerPercentage", null).get()
                .addOnSuccessListener {
                    val productsOffer = it.toObjects(Product::class.java)
                    // check whether the productOffer are equal to the old best products
                    pagingInfo.isPagingEnd = productsOffer == pagingInfo.oldItems
                    // updating the old best products
                    pagingInfo.oldItems = productsOffer
                    viewModelScope.launch {
                        _offerProducts.emit(ResourceWrapper.Success(productsOffer))
                    }
                    pagingInfo.newItemsPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(ResourceWrapper.Error(it.message.toString()))
                    }
                }
        }
    }
}