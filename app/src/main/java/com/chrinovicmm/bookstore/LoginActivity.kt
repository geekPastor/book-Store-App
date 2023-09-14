package com.chrinovicmm.bookstore

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.chrinovicmm.bookstore.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Objects

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase Auth

        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog while login
        progressDialog = ProgressDialog(this)

        progressDialog.setTitle("Veillez patienter")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle not have account, click

        binding.notaccount.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginBtn.setOnClickListener{
            validateData()
        }


    }

    private var email = ""
    private var password =""
    private fun validateData() {
        //input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //data validation

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Format d'email invalide...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()){
            Toast.makeText(this, "Entrez le mot de passe...", Toast.LENGTH_SHORT).show()
        } else{
            loginUser()
        }
    }

    private fun loginUser() {

        //login

        //progress
        progressDialog.setMessage("log in...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "la connexion n'a pas abouti du a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        //chek user role

        progressDialog.setMessage("Checking...")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user role User/Admin
                    val userType = snapshot.child("").value
                    if (userType == "user"){
                        //simple user
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    } else if (userType == "admin"){
                        //admin user
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }
            })
    }
}