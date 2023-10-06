package daniel.brian.ecommerceapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import daniel.brian.ecommerceapp.databinding.ActivityRegisterLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
