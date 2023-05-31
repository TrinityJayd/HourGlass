package com.trinityjayd.hourglass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //get sign up button
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        //set on click listener
        signUpButton.setOnClickListener {
            //go to sign up page
            setContentView(R.layout.activity_home)
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