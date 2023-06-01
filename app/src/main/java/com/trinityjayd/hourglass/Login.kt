package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //get forgot password button
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPasswordButton)
        //set on click listener to forgot password activity
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        //get sign up button
        val signUp = findViewById<Button>(R.id.noAccountButton)
        //set on click listener to sign up activity
        signUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        val password = findViewById<EditText>(R.id.editTextPassword)
        password.transformationMethod = PasswordTransformationMethod()

        val loginButton = findViewById<Button>(R.id.loginButton)
        //set on click listener to home activity
        loginButton.setOnClickListener {
            //get email and password
            val email = findViewById<EditText>(R.id.editTextEmailAddress)


            val validationMethods = ValidationMethods()

            if (email.text.toString().isNullOrBlank()) {
                email.error = "Please enter an email."
                return@setOnClickListener
            } else if (password.text.toString().isNullOrBlank()) {
                password.error = "Please enter a password."
                return@setOnClickListener
            } else if (!validationMethods.isEmailValid(email.text.toString())) {
                email.error = "Please enter a valid email."
                return@setOnClickListener
            } else {
                val emailText = email.text.toString()
                val passwordText = password.text.toString()
                val firebaseVal = FirebaseValidation()

                firebaseVal.isUserExistsWithEmail(emailText) { exists ->
                    if (exists) {
                        auth = Firebase.auth
                        auth.signInWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val intent = Intent(this, Home::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT,
                                    ).show()

                                }
                            }
                    } else {
                        email.error = "User with this email does not exist"
                    }
                }


            }


        }


    }
}