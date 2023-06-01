package com.trinityjayd.hourglass

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
            //get email and password from sign up page
            val email = findViewById<EditText>(R.id.editTextEmailAddress).text.toString()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()
            if (passwordText.equals(confirmPasswordText)) {
                auth = Firebase.auth
                auth.createUserWithEmailAndPassword(email, passwordText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //Toast message
                            Toast.makeText(
                                baseContext, "Sign up successful.",
                                Toast.LENGTH_SHORT,
                            ).show()
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
            //go to sign in page
            setContentView(R.layout.activity_login)
        }

    }
}