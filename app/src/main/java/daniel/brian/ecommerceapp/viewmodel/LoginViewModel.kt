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

@HiltViewModel
class LoginViewModel(
    // injecting firebaseAuth because we will use the firebaseAuth instance in this viewModel
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _login = MutableSharedFlow<ResourceWrapper<FirebaseUser>>()
    val login = _login.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch { _login.emit(ResourceWrapper.Loading()) }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // we will use viewModelScope because emit function is a suspend function and
                // must be called inside a viewScope or another suspend fun
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(ResourceWrapper.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }
}
