package daniel.brian.ecommerceapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.KleineApplication
import daniel.brian.ecommerceapp.data.User
import daniel.brian.ecommerceapp.util.RegisterValidation
import daniel.brian.ecommerceapp.util.ResourceWrapper
import daniel.brian.ecommerceapp.util.validateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    // application provides the content resolver
    app: Application
    // using the androidViewModel to get the content resolver
): AndroidViewModel(app) {

    //getting our states
    private val _user = MutableStateFlow<ResourceWrapper<User>>(ResourceWrapper.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<ResourceWrapper<User>>(ResourceWrapper.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    // function to get user details
    fun getUser() {
        viewModelScope.launch {
            _user.emit(ResourceWrapper.Loading())
        }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                // getting the user object
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(ResourceWrapper.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(ResourceWrapper.Error(it.message.toString()))
                }
            }
    }
    // function to update the user information
    fun updateUser(user: User,imageUri: Uri?) {
        // validating the inputs
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid){
            viewModelScope.launch {
                _user.emit(ResourceWrapper.Error("Check your inputs.."))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(ResourceWrapper.Loading())
        }

        if (imageUri == null){
            saveUserInformation(user,true)
        }else{
            saveUserInformationWithNewImage(user,imageUri)
        }
    }

    /*
    - we're uploading the image on firebase storage
    - To upload an image to firebase we need to get the byteArray of the image
    - we want to upload the image to the firestore and then call the image uri
     */
    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                // getting the image of the image to upload to firestore
                val imageBitmap = MediaStore.Images.Media.getBitmap(getApplication<KleineApplication>().contentResolver,imageUri)
                // getting the byteArrayOutputStream to save image as byteArray
                val byteArrayOutputStream = ByteArrayOutputStream()
                // compressing the image to reduce the size of the image
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,96,byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                // creating a directory in our storage for the image
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                // putting the bytes into image and suspending the function using the await()
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl),false)
            }catch (e: Exception){
                viewModelScope.launch{
                    _updateInfo.emit(ResourceWrapper.Error(e.message.toString()))
                }
            }
        }
    }

    // we're using the "shouldRetrieveOldImage" to determine if we should keep the old image or update it
    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        // using transaction to perform both read and write on the documents
        firestore.runTransaction { transaction ->
            // getting the user document
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrieveOldImage){
                // getting the current user
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                // changing the imagePath to the one we got from current user
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                // using transaction to set the new objects
                transaction.set(documentRef,newUser)
            }else{
                transaction.set(documentRef,user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(ResourceWrapper.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(ResourceWrapper.Error(it.message.toString()))
            }
        }
    }
}