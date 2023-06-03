package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Category

class CategoryMangement {
    private var database = Firebase.database.reference

    fun saveCategory(category: Category){
        val categoryKey = database.push().key // Generate a unique key for the entry
        if (categoryKey != null) {
            database.child("categories").child(categoryKey).setValue(category)
        }
    }

}