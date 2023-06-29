package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.signupButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()

        auth = Firebase.auth

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        val signOut = intent.getBooleanExtra("signOut", false)
        if(signOut){
            auth.signOut()
        }else{
            if (currentUser != null) {
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            }
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //get extra from intent
        val signOut = intent.getBooleanExtra("signOut", false)

        if(signOut){
            moveTaskToBack(true)
        }

    }
}
