package daniel.brian.ecommerceapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int ?= null,
    val selectedSize: String ?= null,
) : Parcelable{
    // we're having a constructor coz we will use the firebase toObjects function
    constructor() :this(Product(),1,null,null)
}
