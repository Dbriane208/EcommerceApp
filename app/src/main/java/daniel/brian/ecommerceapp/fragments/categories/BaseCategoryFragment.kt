package daniel.brian.ecommerceapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.databinding.FragmentBaseCategoryBinding

class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBaseCategoryBinding.inflate(layoutInflater)
        return binding.root
    }
}
