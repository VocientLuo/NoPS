package com.xluo.nops

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.xluo.nops.databinding.ActivityMainBinding
import com.xluo.nops.ui.activity.NewCanvasActivity
import com.xluo.nops.ui.home.HomeFragment
import com.xluo.nops.ui.notifications.NotificationsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val userFragment = NotificationsFragment()
    private val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_activity_main, homeFragment).commit()
        binding.llHome.setOnClickListener {
            binding.llHome.alpha = 1f
            binding.llUser.alpha = 0.5f
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, homeFragment).commit()
        }
        binding.llUser.setOnClickListener {
            binding.llHome.alpha = 0.5f
            binding.llUser.alpha = 1f
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, userFragment).commit()
        }
        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, NewCanvasActivity::class.java))
        }
        binding.llUser.alpha = 0.5f
    }
}