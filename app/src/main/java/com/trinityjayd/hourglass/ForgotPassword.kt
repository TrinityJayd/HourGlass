package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.trinityjayd.hourglass.dbmanagement.UserDbManagement

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //get email edit text
        val email = findViewById<EditText>(R.id.editTextEmailAddress)

        //get change password button
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        //set on click listener to go to login
        changePasswordButton.setOnClickListener {

            //check if email is empty
            if (email.text.toString().isNullOrBlank()) {
                email.error = "Please enter an email"
                return@setOnClickListener
            } else {

                val userDbManagement = UserDbManagement()
                //check if user exists with email
                userDbManagement.isUserExistsWithEmail(email.text.toString()) { exists ->
                    if (exists) {
                        val auth: FirebaseAuth = FirebaseAuth.getInstance()

                        //send password reset email
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