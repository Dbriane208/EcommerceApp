package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    // injecting firebaseAuth because we will use the firebaseAuth instance in this viewModel
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    // We use sharedFlow when we are passing a one time event to the UI
    private val _login = MutableSharedFlow<ResourceWrapper<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<ResourceWrapper<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email: String, password: String) {
        // viewModelScope launches a coroutine that safely emits values into the _login flow. This is important because emitting values
        // from the background thread directly to the UI would lead to threading issues.
        viewModelScope.launch { _login.emit(ResourceWrapper.Loading()) }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // we will use viewModelScope because emit function is a suspend function and
                // must be called inside a viewScope or another suspend fun
                viewModelScope.launch {
                    // This will trigger the success state and emit the data to the flow which will be collected at the LoginFragment
                    it.user?.let {
                        _login.emit(ResourceWrapper.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                // It is triggered when the callback for sign-in options are wrong
                viewModelScope.launch {
                    _login.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String) {
        // emit the values of the _resetPassword flow
        viewModelScope.launch { _resetPassword.emit(ResourceWrapper.Loading()) }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(ResourceWrapper.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }
}
