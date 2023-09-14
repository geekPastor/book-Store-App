package com.chrinovicmm.bookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chrinovicmm.bookstore.databinding.ActivityDashboardAdminBinding
import com.chrinovicmm.bookstore.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUserInfo()

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun checkUserInfo() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            binding.subTitleTv.text = "Non connecte"
        }else{
            //show user info on the screen
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}