package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.User

class UserDbManagement {
    private var database = Firebase.database.reference

    fun addUserToDatabase(user: User) {
        database.child("users").child(user.uid).setValue(user)
    }
}