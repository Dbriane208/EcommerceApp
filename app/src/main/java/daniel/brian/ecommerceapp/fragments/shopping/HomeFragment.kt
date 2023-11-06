package daniel.brian.ecommerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import daniel.brian.ecommerceapp.R
import daniel.brian.ecommerceapp.adapters.HomeViewpagerAdapter
import daniel.brian.ecommerceapp.databinding.FragmentHomeBinding
import daniel.brian.ecommerceapp.fragments.categories.AccessoryFragment
import daniel.brian.ecommerceapp.fragments.categories.ChairFragment
import daniel.brian.ecommerceapp.fragments.categories.CupboardFragment
import daniel.brian.ecommerceapp.fragments.categories.FurnitureFragment
import daniel.brian.ecommerceapp.fragments.categories.MainCategoryFragment
import daniel.brian.ecommerceapp.fragments.categories.TableFragment

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf(
            MainCategoryFragment(),
            AccessoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            FurnitureFragment(),
            TableFragment(),
        )

        val viewPager2Adapter =
            HomeViewpagerAdapter(categoriesFragment, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tableLayout, binding.viewPagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Accessory"
                2 -> tab.text = "Chair"
                3 -> tab.text = "Cupboard"
                4 -> tab.text = "Furniture"
                5 -> tab.text = "Tables"
            }
        }.attach()
    }
}
