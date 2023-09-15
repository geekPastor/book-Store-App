package com.chrinovicmm.bookstore

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var handler: Handler? = null
    //handler = new Handler(Looper.getMainLooper());
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper());
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        handler!!.postDelayed(Runnable{
            checkUserInfo()
        }, 1000)
    }

    private fun checkUserInfo() {
        var firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else{
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        //get user role User/Admin
                        val userType = snapshot.child("").value
                        if (userType == "user"){
                            //simple user
                            startActivity(Intent(this@SplashActivity, DashboardUserActivity::class.java))
                            finish()
                        } else if (userType == "admin"){
                            //admin user
                            startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                            finish()
                        }
                    }
                })
        }

    }
}