package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.CartProduct
import daniel.brian.ecommerceapp.firebase.FirebaseCommon
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth ,// we want to get the id of the signed-in user
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<ResourceWrapper<CartProduct>>(ResourceWrapper.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct){
        // we're using the auth.uid to get the signed in user. We're attaching the cart details to a certain user
        firestore.collection("user").document(auth.uid!!).collection("cart")
            // comparing the existing nested product id to new cart product id
            .whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {it ->
                // if we use it.documents alone we will get only one product because we're filtering with id
                it.documents.let {
                    if (it.isEmpty()){
                        // Add a new product
                        addNewProduct(cartProduct)
                    } else{
                       val product = it.first().toObject(cartProduct::class.java)
                       if(product == cartProduct){
                           // Increase the quantity
                           val documentId = it.first().id
                           increaseQuantity(documentId,cartProduct)
                       }else{
                           //Add a new product
                           addNewProduct(cartProduct)
                       }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(ResourceWrapper.Error(it.message.toString())) }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){addedProduct, e ->
            viewModelScope.launch {
                if (e == null){
                    _addToCart.emit(ResourceWrapper.Success(addedProduct!!))
                }else{
                    _addToCart.emit(ResourceWrapper.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String,cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){_,e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(ResourceWrapper.Success(cartProduct))
                }else{
                    _addToCart.emit(ResourceWrapper.Error(e.message.toString()))
                }
            }
        }
    }
}