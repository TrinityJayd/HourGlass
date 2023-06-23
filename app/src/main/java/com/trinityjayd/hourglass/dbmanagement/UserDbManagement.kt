package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.User

class UserDbManagement {
    private var database = Firebase.database.reference
    private lateinit var auth: FirebaseAuth

    fun addUserToDatabase(user: User) {
        database.child("users").child(user.uid).setValue(user)
    }

    fun isUserExistsWithEmail(email: String, onComplete: (Boolean) -> Unit) {
        auth = FirebaseAuth.getInstance()
        //check if user exists
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val userExists = !signInMethods.isNullOrEmpty()
                    onComplete(userExists)
                } else {
                    onComplete(false)
                }
            }
    }

    //get user name
    fun getUserFullName(uid: String, callback: (String?) -> Unit) {
        //get users name
        val userRef = database.child("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                val fullName = user?.fullName
                callback(fullName)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }


}