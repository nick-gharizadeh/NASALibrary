package com.project.nasalibrary.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.project.nasalibrary.ui.SearchFragment.SearchFragment
import com.project.nasalibrary.R
import com.project.nasalibrary.databinding.ActivityMainBinding
import com.project.nasalibrary.ui.HomeFragment.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Lottie animation setup
        binding.lottieAnimationView.playAnimation()
        binding.lottieAnimationView.animate().setDuration(4000).alpha(1f).withEndAction {
            binding.lottieAnimationView.visibility = View.GONE
            binding.hostFragment.visibility = View.VISIBLE
            binding.bottomNavigation.visibility = View.VISIBLE
        }


        // Bottom navigation setup
        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        setCurrentFragment(homeFragment)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    setCurrentFragment(homeFragment)
                    true
                }

                R.id.search -> {
                    setCurrentFragment(searchFragment)
                    true
                }

                else -> false
            }
        }


    }


    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.hostFragment, fragment)
            commit()
        }


}