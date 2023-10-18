package daniel.brian.ecommerceapp.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val imagePath: String = "",
) {
    // Firebase uses the constructor
    constructor() : this("", "", "", "")
}
