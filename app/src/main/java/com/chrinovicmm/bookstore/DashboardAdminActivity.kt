package com.chrinovicmm.bookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chrinovicmm.bookstore.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUserInfo()

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUserInfo()
        }
    }

    private fun checkUserInfo() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //redirection to main the screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            //show user info on the screen
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}