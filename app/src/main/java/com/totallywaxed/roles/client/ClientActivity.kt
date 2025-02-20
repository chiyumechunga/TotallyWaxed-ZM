package com.totallywaxed.roles.client

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment
import com.totallywaxed.R
import com.totallywaxed.databinding.ActivityClientBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Connect Bottom Navigation with Navigation Controller
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}