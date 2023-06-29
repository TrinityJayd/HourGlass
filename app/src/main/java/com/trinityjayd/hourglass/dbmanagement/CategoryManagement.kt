package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Category

class CategoryManagement {
    private var database = Firebase.database.reference

    fun saveCategory(category: Category) {
        //save category to database
        database.child("categories").child(category.uid).child(category.name).setValue(category)
    }

    fun isCategoryExists(uid: String, categoryName: String, callback: (Boolean) -> Unit) {
        val categoryRef = database.child("categories").child(uid)
        categoryRef.keepSynced(true)
        //check if category exists already with the same name for the current user
        categoryRef.child(categoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val exists = dataSnapshot.exists()
                    callback(exists)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error, if needed
                    callback(false)
                }
            })
    }

    fun getCategoryColor(category: String, callback: (Int) -> Unit) {
        //get current user
        val uid = Firebase.auth.currentUser?.uid ?: return
        val database = Firebase.database.reference
        val categoryReference = database.child("categories").child(uid).child(category)

        //get category color
        categoryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    category?.let {
                        callback.invoke(it.color)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }









}