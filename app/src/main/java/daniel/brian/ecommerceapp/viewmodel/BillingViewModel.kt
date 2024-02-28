package daniel.brian.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.data.Address
import daniel.brian.ecommerceapp.util.ResourceWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _address = MutableStateFlow<ResourceWrapper<List<Address>>>(ResourceWrapper.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddresses()
    }
    private fun getUserAddresses() {
       viewModelScope.launch { _address.emit(ResourceWrapper.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null){
                    viewModelScope.launch { _address.emit(ResourceWrapper.Error(error.message.toString())) }
                    return@addSnapshotListener
                }

                val address = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(ResourceWrapper.Success(address!!)) }
            }
    }
}