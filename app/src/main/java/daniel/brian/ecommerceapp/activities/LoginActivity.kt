package daniel.brian.ecommerceapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.ecommerceapp.databinding.ActivityRegisterLoginBinding
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // This will close the app when back button is pressed from LoginActivity
        finishAffinity()
    }
}
