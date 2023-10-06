package daniel.brian.ecommerceapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import daniel.brian.ecommerceapp.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
