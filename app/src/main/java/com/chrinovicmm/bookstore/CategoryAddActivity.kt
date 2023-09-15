package com.chrinovicmm.bookstore

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chrinovicmm.bookstore.databinding.ActivityCategoryAddBinding
import com.chrinovicmm.bookstore.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.zip.Inflater

class CategoryAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Veuilez patienter")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var category = ""
    private fun validateData() {

        category = binding.categoryEt.text.toString().trim()

        if (category.isEmpty()){
            Toast.makeText(this, "Veuillez entrez une categorie...", Toast.LENGTH_SHORT).show()
        }else{
            addDatadb()
        }
    }

    private fun addDatadb() {

        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"


        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("${timestamp}")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Enregistrement effectue avec succes", Toast.LENGTH_SHORT).show()

            }

            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Echec d'enregistrement du a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}