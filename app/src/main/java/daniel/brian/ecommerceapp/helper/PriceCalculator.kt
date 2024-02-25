package daniel.brian.ecommerceapp.helper

fun Float?.getProductPrice(price: Float): Float{
    //this --> percentage
    if (this == null)
        return  price
    val remainingPricePercentage = 1f - this

    return remainingPricePercentage * price
}