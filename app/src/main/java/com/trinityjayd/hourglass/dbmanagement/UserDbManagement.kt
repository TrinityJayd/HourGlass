package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.User

class UserDbManagement {
    private var database = Firebase.database.reference
    private var auth = Firebase.auth

    fun addUserToDatabase(user: User) {
        database.child("users").child(user.uid).setValue(user)
    }

    fun isUserExistsWithEmail(email: String, onComplete: (Boolean) -> Unit) {
        //check if the user exists
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                val hasEmailProvider =
                    signInMethods?.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)
                        ?: false
                val hasGoogleProvider =
                    signInMethods?.contains(GoogleAuthProvider.PROVIDER_ID) ?: false

                val exists = hasEmailProvider || hasGoogleProvider
                onComplete(exists)
            } else {
                onComplete(false)
            }
        }

    }

    fun isUserExistsWithUid(uid: String, onComplete: (Boolean) -> Unit) {
        //check if the user exists
        val userRef = database.child("users").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(false)
            }
        })
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

    fun deleteUser(uid: String) {
        //delete user and data from database
        Firebase.auth.currentUser?.delete()
        database.child("users").child(uid).removeValue()
        database.child("goals").child(uid).removeValue()
        database.child("entries").child(uid).removeValue()
        database.child("categories").child(uid).removeValue()
    }

    //check if user has a google account
    fun isGoogleAccountExists(email: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val isSignedInWithGoogle = signInMethods?.contains("google.com") == true
                    onComplete(isSignedInWithGoogle)
                } else {
                    onComplete(false)
                }
            }
    }


}


