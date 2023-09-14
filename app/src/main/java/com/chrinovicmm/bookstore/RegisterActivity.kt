package com.chrinovicmm.bookstore

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.chrinovicmm.bookstore.databinding.ActivityMainBinding
import com.chrinovicmm.bookstore.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase Auth

        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog while login or register
        progressDialog = ProgressDialog(this)

        progressDialog.setTitle("Veillez patienter")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle back button click
        binding.backBtn.setOnClickListener {
            onBackPressed() //go to previous screen
        }

        binding.registerBtn.setOnClickListener{
            validateData()
        }
    }


    private var name = ""
    private var email = ""
    private var password = ""
    private fun validateData() {
        //input data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val confirm_password = binding.confirmPasswordEt.text.toString().trim()


        //validate data

        if (name.isEmpty()){
            Toast.makeText(this, "Entrer votre nom...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Entrer une bonne adresse email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()){
            Toast.makeText(this, "Entrer le mot de passe...", Toast.LENGTH_SHORT).show()
        } else if (confirm_password.isEmpty()){
            Toast.makeText(this, "Confirmez votre mot de passe...", Toast.LENGTH_SHORT).show()
        } else if (password != confirm_password){
            Toast.makeText(this, "Les champs mot de passe et Confirmer mo de passe doivent avoir la meme valuer...", Toast.LENGTH_SHORT).show()
        } else{
            createUser()
        }
    }

    //create user - Firebase Auth
    private fun createUser() {
        //progress

        progressDialog.setMessage("Creation du compte....")
        progressDialog.show()

        //create user

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //user account created
                updateUserInfo()
            }
            .addOnFailureListener{ e->
                //failed creating user account
                progressDialog.dismiss()
                Toast.makeText(this, "La creation du compte n'a pas abouti a cause de ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }

    //save data in the data base
    private fun updateUserInfo() {
        //save user date in the data base

        progressDialog.setMessage("Enregisterment en cours...")


        val timestamp = System.currentTimeMillis()

        //get user uid

        val uid = firebaseAuth.uid

        //setup data
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["password"] = password
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //set data in the db
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //add user inf in the data base
                progressDialog.dismiss()
                Toast.makeText(this, "Compte creer avec succes", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "La creation du compte n'a pas abouti a cause de ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}