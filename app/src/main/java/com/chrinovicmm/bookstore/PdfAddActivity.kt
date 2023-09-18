package com.chrinovicmm.bookstore

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.chrinovicmm.bookstore.databinding.ActivityPdfAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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

        binding.attachPdfBtn.setOnClickListener {
            pdfPickIntent()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

            binding.backBtn.setOnClickListener {
                onBackPressed()
            }
    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
        Log.d(TAG, "validateData: validation des donnees")

        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.descriptionEt.text.toString().trim()

        if (title.isEmpty()){
            Toast.makeText(this, "Entrez le titre...", Toast.LENGTH_SHORT).show()
        }

        else if  (description.isEmpty()){
            Toast.makeText(this, "Entrez la description...", Toast.LENGTH_SHORT).show()
        }

        else if  (category.isEmpty()){
            Toast.makeText(this, "Choisissez la categorie...", Toast.LENGTH_SHORT).show()
        }
        else if  (category == null){
            Toast.makeText(this, "Choisissez une ressource a partager...", Toast.LENGTH_SHORT).show()
        }
        else{
            uploadPdfToStorage()
        }

    }

    private fun uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: chargement dans la base des donnees")

        progressDialog.setMessage("Chargement du fichier en cours")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathName = "Books/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {taskSnapshot->
                Log.d(TAG, "uploadPdfToStorage: Chargement reussi obtention de l'url")

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);

                val uploadedUrl = "${uriTask.result}"

                uploadPdfInfoToDb(uploadedUrl, timestamp)
            }
            .addOnFailureListener{e->
                Log.d(TAG, "Echec du chargement de la ressource du a ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Echec du chargement de la ressource du a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadPdfInfoToDb: Chargement dans la base des donnees")
        progressDialog.setMessage("Chargement dans la base des donnees")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["url"] = "$uploadedUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewCount"] = 0
        hashMap["downloadsCount"] = 0


        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb: chargement dans la bdd")

                progressDialog.dismiss()
                Toast.makeText(this, "Charger...", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }
            .addOnFailureListener{e->
                Log.d(TAG, "Echec du chargement de la ressource du a ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Echec du chargement de la ressource du a ${e.message}", Toast.LENGTH_SHORT).show()
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
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "PDF Picked:")
            val data: Intent? = result.data
            val pdfUri: Uri? = data?.data
            // Utilisez pdfUri comme nécessaire
        } else {
            Log.d(TAG, "PDF Picked:")
            Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show()
        }
    }
}