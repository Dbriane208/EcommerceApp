package daniel.brian.ecommerceapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // using parcelize because we'll be parsing this class between our fragments
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
): Parcelable{
    constructor(): this("","","","","","")
}
