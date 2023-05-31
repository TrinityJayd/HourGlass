package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //get login button
        val loginButton = findViewById<Button>(R.id.loginButton)
        //set on click listener to home activity
        loginButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

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



    }
}