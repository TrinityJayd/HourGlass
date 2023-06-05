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
import com.trinityjayd.hourglass.dbmanagement.UserDbManagement
import com.trinityjayd.hourglass.models.User


class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //get password and confirm password edit texts
        val password = findViewById<EditText>(R.id.editTextPassword)
        password.transformationMethod = PasswordTransformationMethod()

        val confirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        confirmPassword.transformationMethod = PasswordTransformationMethod()


        //get sign up button
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            //get full name and email edit texts
            val fullname = findViewById<EditText>(R.id.editTextFullName)

            val email = findViewById<EditText>(R.id.editTextEmailAddress)

            val validationMethods = ValidationMethods()

            //check if fields are empty
            if (fullname.text.toString().isNullOrBlank()) {
                fullname.error = "Please enter your full name"
                return@setOnClickListener
            } else if (!validationMethods.onlyLetters(fullname.text.toString())) {
                fullname.error = "Please enter a valid name"
                return@setOnClickListener
            } else if (email.text.toString().isNullOrBlank()) {
                email.error = "Please enter an email"
                return@setOnClickListener
            } else if (password.text.toString().isNullOrBlank()) {
                password.error = "Please enter a password"
                return@setOnClickListener
            } else if (confirmPassword.text.toString().isNullOrBlank()) {
                confirmPassword.error = "Please confirm your password"
                return@setOnClickListener
            } else if (!password.text.toString().equals(confirmPassword.text.toString())) {
                //check if passwords match
                confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            } else if (!validationMethods.isEmailValid(email.text.toString())) {
                //check if email is valid
                email.error = "Please enter a valid email."
                return@setOnClickListener
            } else if (!validationMethods.isPasswordValid(password.text.toString())) {
                //check if password is valid
                password.error =
                    "Passwords must be at least 8 characters long and contain at least one number, one uppercase letter, one lowercase letter, one special character and cannot contain spaces."
                return@setOnClickListener
            } else {
                val fullName = fullname.text.toString()
                val emailText = email.text.toString()
                val passwordText = password.text.toString()

                auth = Firebase.auth
                //create user with email and password
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            //create a user object
                            val newUser = User(user!!.uid, fullName)
                            UserDbManagement().addUserToDatabase(newUser)

                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
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