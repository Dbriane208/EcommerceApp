package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.User
import daniel.brian.ecommerceapp.util.RegisterFieldsState
import daniel.brian.ecommerceapp.util.RegisterValidation
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.util.validateEmail
import daniel.brian.ecommerceapp.util.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel // enables us to do DI without having a viewModelFactory
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _register =
        MutableStateFlow<ResourceWrapper<FirebaseUser>>(ResourceWrapper.Unspecified())
    val register: Flow<ResourceWrapper<FirebaseUser>> = _register

    // Channel don't receive initial value as compared to Flow
    private val _validation = Channel<RegisterFieldsState>()

    // Setting it as a Flow will allow the use of validation to pull the registerField data using the collect in the register fragment
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            // blocks the current thread(main thread) until the code in it is executed
            runBlocking {
                _register.emit(ResourceWrapper.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { it ->
                    it.user?.let {
                        _register.value = ResourceWrapper.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = ResourceWrapper.Error(it.message.toString())
                }
        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email),
                validatePassword(password),
            )
            // We're using send by the fact that we're using Channel to push the registerFieldState to the pull-based Flow
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }
}
