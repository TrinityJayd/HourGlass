package com.trinityjayd.hourglass

import com.google.firebase.auth.FirebaseAuth

class FirebaseValidation {


    fun isUserExistsWithEmail(email: String, onComplete: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val userExists = signInMethods != null && signInMethods.isNotEmpty()
                    onComplete(userExists)
                } else {
                    onComplete(false)
                }
            }
    }
}