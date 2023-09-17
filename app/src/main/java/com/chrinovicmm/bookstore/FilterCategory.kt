package com.chrinovicmm.bookstore

import android.widget.Filter

class FilterCategory: Filter {
    private var filterList: ArrayList<ModelCategory>
    private var adapterCategory: AdapterCategory

    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val result = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            val filteredModels: ArrayList<ModelCategory> = ArrayList()

            for (i in 0 until filterList.size){
                if (filterList[i].category.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }

            result.count = filteredModels.size
            result.values = filteredModels
        }
        else{
            result.count = filterList.size
            result.values = filterList
        }

        return result
    }

    override fun publishResults(constraint: CharSequence?, result: FilterResults) {
        adapterCategory.categoryArrayList = result.values as ArrayList<ModelCategory>
        adapterCategory.notifyDataSetChanged()
    }

}