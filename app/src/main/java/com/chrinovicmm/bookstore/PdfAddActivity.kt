package com.chrinovicmm.bookstore

import android.app.Activity
import android.app.Application
import androidx.activity.result.ActivityResult
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.chrinovicmm.bookstore.databinding.ActivityPdfAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryArraList: ArrayList<ModelCategory>
    private var pdfUri: Uri? = null
    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        laodPdfCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Veuillez patienter")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.categoryTv.setOnClickListener{
            categoryPickDialog()
        }
    }

    private fun laodPdfCategories() {
        Log.d(TAG, "laodPdfCategories: Loading PDF categories")

        categoryArraList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArraList.clear()
                for (ds in snapshot.children){
                    val model =  ds.getValue(ModelCategory::class.java)

                    categoryArraList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    private fun categoryPickDialog(){
        Log.d(TAG, "categoryPickDialog: Affichage de la boite de dialogue de la selection des categories de PDF")

        val categoriesArray = arrayOfNulls<String>(categoryArraList.size)
        for (i in categoriesArray.indices){
            categoriesArray[i] = categoryArraList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick category")
            .setItems(categoriesArray){dialog, which->

                selectedCategoryTitle = categoryArraList[which].category
                selectedCategoryId = categoryArraList[which].id

                binding.categoryTv.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: l'ID de la categorie selectionne est : $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: le nom de la categorie selectionne est : $selectedCategoryTitle")
            }
            .show()
    }

    private fun pdfPickIntent(){
        Log.d(TAG, "Debut de la selection du PDF")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{result->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF Picked: ")
                pdfUri = result.data?.data
            }
            else{
                Log.d(TAG, "PDF Picked: ")
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}