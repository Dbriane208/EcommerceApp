package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import daniel.brian.ecommerceapp.data.Category
import daniel.brian.ecommerceapp.data.Product
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// in this class we cannot use dagger hilt because it cannot allow us
// to pass the category inside the constructor so we'll create a viewModelFactory
class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel(){

    private val _offerProducts = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<ResourceWrapper<List<Product>>>(ResourceWrapper.Unspecified())
    val bestProducts = _bestProducts.asSharedFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    private fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProducts.emit(ResourceWrapper.Loading())
        }

        firestore.collection("products").whereEqualTo("category",category.category)
            .whereEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val productsBest = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(ResourceWrapper.Success(productsBest))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }

    private fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(ResourceWrapper.Loading())
        }

        firestore.collection("products").whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val productsOffer = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(ResourceWrapper.Success(productsOffer))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch{
                    _offerProducts.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }
}