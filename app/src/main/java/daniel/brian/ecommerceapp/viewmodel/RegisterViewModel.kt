package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.User
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel // enables us to do DI without having a viewModelFactory
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _register = MutableStateFlow<ResourceWrapper<FirebaseUser>>(ResourceWrapper.Unspecified())
    val register: Flow<ResourceWrapper<FirebaseUser>> = _register

    fun createAccountWithEmailAndPassword(user: User, password: String) {
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
    }
}
