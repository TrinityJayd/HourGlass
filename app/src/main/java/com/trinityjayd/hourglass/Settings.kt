package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.UserDbManagement

class Settings : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //get current user
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        //create user db management object
        val userDbManagement = UserDbManagement()


        //get name text view
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        //get email text view
        val emailTextView = findViewById<TextView>(R.id.emailTextView)

        //set name text view to user's name
        userDbManagement.getUserFullName(uid) { fullName ->
            nameTextView.text = fullName
        }

        //set email text view to user's email
        emailTextView.text = currentUser.email

        //get home Image View
        val homeImageView = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        homeImageView.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //get sign out button
        val signOutButton = findViewById<Button>(R.id.signOutButton)
        //set on click listener
        signOutButton.setOnClickListener {
            auth.signOut()

            //check if user signed in with google
            val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
            if (googleSignInAccount != null) {
                //sign user out of google

                //get google sign in client
                val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                //sign out of google
                googleSignInClient.signOut()
            }

            //prevent user from clicking back button to go back to home


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}