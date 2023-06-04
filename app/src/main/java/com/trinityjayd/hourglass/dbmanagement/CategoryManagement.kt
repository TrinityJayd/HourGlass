package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Category

class CategoryManagement {
    private var database = Firebase.database.reference

    fun saveCategory(category: Category) {
        database.child("categories").child(category.uid).child(category.name).setValue(category)
    }






}