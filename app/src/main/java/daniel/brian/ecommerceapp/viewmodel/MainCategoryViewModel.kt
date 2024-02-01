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

    init {
        fetchSpecialProducts()
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