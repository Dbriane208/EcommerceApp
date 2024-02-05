package daniel.brian.ecommerceapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.util.Constants.INTRODUCTION_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    firebaseAuth: FirebaseAuth
): ViewModel() {
    // providing state
    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    // declaring constants
    companion object{
        const val SHOPPING_ACTIVITY = 23
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }

    // init blocks are used to initialize properties of a class
    // they are executed when an instance of a class is created
    // There can be one or more init blocks in a class
    init {
        // checking whether the user is registered or the button is clicked
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY,false)
        val user = firebaseAuth.currentUser

        // checking whether the user is null so that we can navigate to other activities
        if (user != null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked){
            // navigating to the accounts options when the button is clicked
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        }
        else{
            Unit
        }
    }

    // function to change the value inside sharedPreferences
    fun startButtonClick(){
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }
}