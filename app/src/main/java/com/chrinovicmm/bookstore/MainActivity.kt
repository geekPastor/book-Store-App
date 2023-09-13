package com.chrinovicmm.bookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chrinovicmm.bookstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //clique sur login

        binding.iconIv.setOnClickListener{

            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.skipBtn.setOnClickListener {

            startActivity(Intent(this, DashboardUserActivity::class.java))
        }

    }
}