package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.User
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){
    // creating a user state
    private val _user = MutableStateFlow<ResourceWrapper<User>>(ResourceWrapper.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        // emit a loading state
        viewModelScope.launch {
            _user.emit(ResourceWrapper.Loading())
        }

        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                // emit an error state incase of an error
                if (error != null){
                    viewModelScope.launch {
                        _user.emit(ResourceWrapper.Error(error.message.toString()))
                    }
                } else {
                    // creating a user object which should not be null
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(ResourceWrapper.Success(user))
                        }
                    }
                }
            }
    }
}