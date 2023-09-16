package com.chrinovicmm.bookstore

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chrinovicmm.bookstore.databinding.RowCategoryBinding

class AdapterCategory :RecyclerView.Adapter<AdapterCategory.HolderCategory>{

    private val context: Context
    private val categoryArrayList: ArrayList<ModelCategory>
    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        TODO("Not yet implemented")
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){
        var category:TextView = binding.categoryTv
        var deleteBtn:ImageButton = binding.deleteBtn
    }
}