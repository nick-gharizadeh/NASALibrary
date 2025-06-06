package com.project.nasalibrary.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.project.nasalibrary.R
import com.project.nasalibrary.databinding.ActivityMainBinding
import com.project.nasalibrary.ui.detailFragment.DetailFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.inflationx.viewpump.ViewPumpContextWrapper


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DetailFragment.FullscreenListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)


        // Lottie animation setup
        binding.lottieAnimationViewLogo.playAnimation()
        binding.lottieAnimationViewLogo.animate().setDuration(4000).alpha(1f).withEndAction {
            binding.lottieAnimationGroup.visibility = View.GONE
            binding.hostFragment.visibility = View.VISIBLE
            binding.bottomNavigation.visibility = View.VISIBLE
        }

    }

    override fun toggleActivityUIForFullscreen(isFullscreen: Boolean) {
        binding.bottomNavigation.visibility = if (isFullscreen) View.GONE else View.VISIBLE
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }


}