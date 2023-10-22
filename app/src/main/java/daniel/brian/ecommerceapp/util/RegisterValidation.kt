package daniel.brian.ecommerceapp.util

// sealed classes are usually used to represent an operation that can have many outcomes
sealed class RegisterValidation {
    // object meaning it is a singleton instance of the class
    object Success : RegisterValidation()

    // contains a message that explains why the error occurred
    data class Failed(val message: String) : RegisterValidation()
}

// contains the fields we want to confirm authentication with
// the class keeps track of the validation status of the registration fields
data class RegisterFieldsState(
    // Both of them hold the validation status that is either success or failed
    val email: RegisterValidation,
    val password: RegisterValidation,
)
