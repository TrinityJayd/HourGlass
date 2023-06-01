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
        val password = findViewById<EditText>(R.id.editTextPassword)
        password.transformationMethod = PasswordTransformationMethod()

        val confirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        confirmPassword.transformationMethod = PasswordTransformationMethod()

        //get change password button
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        //set on click listener to go to login
        changePasswordButton.setOnClickListener {
            val validationMethods = ValidationMethods()

            if (email.text.toString().isNullOrBlank()) {
                email.error = "Please enter an email"
                return@setOnClickListener
            } else if (password.text.toString().isNullOrBlank()) {
                password.error = "Please enter a password"
                return@setOnClickListener
            } else if (confirmPassword.text.toString().isNullOrBlank()) {
                confirmPassword.error = "Please confirm your password"
                return@setOnClickListener
            } else if (!password.text.toString().equals(confirmPassword.text.toString())) {
                confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            } else if (!validationMethods.isEmailValid(email.text.toString())) {
                email.error = "Please enter a valid email."
                return@setOnClickListener
            } else if (!validationMethods.isPasswordValid(password.text.toString())) {
                password.error =
                    "Passwords must be at least 8 characters long and contain at least one number, one uppercase letter, one lowercase letter, one special character and cannot contain spaces."
                return@setOnClickListener
            }else{
                val auth: FirebaseAuth = FirebaseAuth.getInstance()

                auth.sendPasswordResetEmail(email.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        }
                    }
            }
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
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