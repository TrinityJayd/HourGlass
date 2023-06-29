package com.trinityjayd.hourglass

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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


        val homeImageView = findViewById<ImageView>(R.id.homeImageView)
        homeImageView.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            auth.signOut()

            //check if user signed in with google
            val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
            if (googleSignInAccount != null) {
                //sign user out of google

                //get google sign in client
                val googleSignInClient =
                    GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                //sign out of google
                googleSignInClient.signOut()
            }


            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("signOut", true)
            startActivity(intent)
        }

        //get delete account TextView
        val deleteAccountButton = findViewById<TextView>(R.id.deleteAccountTextView)

        if (isInternetAvailable()) {
            //set on click listener
            deleteAccountButton.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Account")
                alertDialogBuilder.setMessage("This action cannot be undone.\nAre you sure you want to delete your account?")
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    //delete the google credentials
                    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
                    if (googleSignInAccount != null) {
                        //sign user out of google

                        //get google sign in client
                        val googleSignInClient =
                            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                        //sign out of google
                        googleSignInClient.signOut()
                    }

                    val userDbManagement = UserDbManagement()
                    userDbManagement.deleteUser(uid)


                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("signOut", true)
                    startActivity(intent)

                }
                alertDialogBuilder.setNegativeButton("No") { _, _ ->
                    //do nothing
                }

                alertDialogBuilder.show()
            }
        } else {
            deleteAccountButton.setOnClickListener {
                Toast.makeText(
                    this,
                    "Please connect to the internet to delete your account",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }


    //check if device is connected to internet
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }


}