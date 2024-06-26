package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.CartProduct
import daniel.brian.ecommerceapp.firebase.FirebaseCommon
import daniel.brian.ecommerceapp.helper.getProductPrice
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore, //accessing the firestore storage
    private val auth: FirebaseAuth, // accessing the user uid
    private val firebaseCommon: FirebaseCommon // accessing the increase and decrease quantity functions
): ViewModel() {
    // create our state and making it public
    private val _cartProducts = MutableStateFlow<ResourceWrapper<List<CartProduct>>>(ResourceWrapper.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    // creating a state to delete item from cart if quantity == 0
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    // creating a list so that we can get the index of document we want
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    // using the cartProducts flow because our product prices depends on that flow
    val productsPrice = cartProducts.map {
        when(it){
            is ResourceWrapper.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float {
        // it will return a double which we will then convert into a float
        return  data.sumOf { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            // we're using the snapshot listener because we want to update our cart once a product is added
            .addSnapshotListener { value, error ->
                // checking whether value and error are null
                if (error != null || value == null ){
                    viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Error(error?.message.toString())) }
                }else{
                    // this block of code will emit a result success
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Success(cartProducts)) }
                }
            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        // getting the exact location index of our cart item from our cart products state
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /*
        * the index could be equal to -1 if the function [getCartProducts] delays which will also delay the result we expect
        * to be inside the [_cartProducts] and to prevent the app from crashing we make a check
        */

        if (index != null && index != -1){
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    // to implement the loading of the progress bar
                    viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    if (cartProduct.quantity == 1){
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ _, exception ->
            if (exception != null){
                viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Error(exception.message.toString())) }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ _, exception ->
            if (exception != null){
                viewModelScope.launch { _cartProducts.emit(ResourceWrapper.Error(exception.message.toString())) }
            }
        }
    }

    fun deleteCartProducts(cartProduct: CartProduct) {
       val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1){
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }
}