package daniel.brian.ecommerceapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.data.CartProduct

class FirebaseCommon(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val cartCollection = firestore.collection("user").document(auth.uid!!).collection("cart")

    fun addProductToCart(cartProduct: CartProduct,onResult:(CartProduct?,Exception?) -> Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }
            .addOnFailureListener {
                onResult(null,it)
            }
    }

    /*
    - Firebase transaction ensures that all transactions happens at once. If one transaction fails everything in that
    transaction fails. We'll use firebase transaction to receive our products,increase the quantity and saved the updated products.
    - runBatch - used to only read documents from firestore
    - runTransaction - used to read and write documents from firestore
    - we'll use transaction in our case because we want to read and update our quantity
    */
    fun increaseQuantity(documentId: String,onResult: (String?, Exception?) -> Unit){
         firestore.runTransaction { transaction ->
             // getting the path of our cart product document
             val documentRef = cartCollection.document(documentId)
             // getting our cart product document
             val document = transaction.get(documentRef)
             // getting the object of the above document
             val productObject = document.toObject(CartProduct::class.java)
             // checking whether the projectObject is null
             productObject?.let { cartProduct ->
                 val newQuantity = cartProduct.quantity + 1
                 // copying the cart product object and changing only one data object
                 val newProductObject = cartProduct.copy(quantity = newQuantity)
                 // passing the document we want to update
                 transaction.set(documentRef,newProductObject)
             }

         }.addOnSuccessListener {
                 onResult(documentId,null)
         }.addOnFailureListener {
                 onResult(null,it)
             }
    }
}