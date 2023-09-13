package com.chrinovicmm.bookstore

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

        }

        binding.skipBtn.setOnClickListener {

        }

    }
}