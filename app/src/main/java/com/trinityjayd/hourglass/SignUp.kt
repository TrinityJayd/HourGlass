package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        val password = findViewById<EditText>(R.id.editTextPassword)
        password.transformationMethod = PasswordTransformationMethod()

        val confirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        confirmPassword.transformationMethod = PasswordTransformationMethod()


        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val fullname = findViewById<EditText>(R.id.editTextFullName)

            val email = findViewById<EditText>(R.id.editTextEmailAddress)

            if(fullname.text.toString().isNullOrBlank()){
                fullname.error = "Please enter your full name"
                return@setOnClickListener
            } else if(email.text.toString().isNullOrBlank()){
                email.error = "Please enter an email"
                return@setOnClickListener
            } else if (password.text.toString().isNullOrBlank()) {
                password.error = "Please enter a password"
                return@setOnClickListener
            } else if (confirmPassword.text.toString().isNullOrBlank()) {
                confirmPassword.error = "Please confirm your password"
                return@setOnClickListener
            }else if(!password.text.toString().equals(confirmPassword.text.toString())){
                confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            } else {
                val emailText = email.text.toString()
                val passwordText = password.text.toString()
                auth = Firebase.auth
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            setContentView(R.layout.activity_home)
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Sign Up failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }


        //get sign in button
        val signInButton = findViewById<Button>(R.id.signInButton)
        //set on click listener
        signInButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}