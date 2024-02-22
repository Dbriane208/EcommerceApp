package daniel.brian.ecommerceapp.util

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import daniel.brian.ecommerceapp.activities.ShoppingActivity

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        daniel.brian.ecommerceapp.R.id.bottomNavigation
    )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        daniel.brian.ecommerceapp.R.id.bottomNavigation
    )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}