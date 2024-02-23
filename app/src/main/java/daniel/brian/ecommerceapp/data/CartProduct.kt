package daniel.brian.ecommerceapp.data

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int ?= null,
    val selectedSize: String ?= null,
){
    // we're having a constructor coz we will use the firebase toObjects function
    constructor() :this(Product(),1,null,null)
}
