package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //get email edit text
        val email = findViewById<EditText>(R.id.editTextEmailAddress)

        //get change password button
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        //set on click listener to go to login
        changePasswordButton.setOnClickListener {

            if (email.text.toString().isNullOrBlank()) {
                email.error = "Please enter an email"
                return@setOnClickListener
            } else {
                val firebaseVal = FirebaseValidation()

                firebaseVal.isUserExistsWithEmail(email.text.toString()) { exists ->
                    if (exists) {
                        val auth: FirebaseAuth = FirebaseAuth.getInstance()

                        auth.sendPasswordResetEmail(email.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                }
                            }
                    } else {
                        email.error = "User with this email does not exist"
                    }
                }
            }

        }

        //get cancel button
        val cancelButton = findViewById<Button>(R.id.backButton)
        //set on click listener to go to login
        cancelButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}