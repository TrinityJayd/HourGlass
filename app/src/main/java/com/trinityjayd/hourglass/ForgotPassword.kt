package com.trinityjayd.hourglass

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.trinityjayd.hourglass.dbmanagement.UserDbManagement

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //get email edit text
        val email = findViewById<EditText>(R.id.editTextEmailAddress)

        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.hide()

        //get change password button
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)

        if(isInternetAvailable()){
            changePasswordButton.setOnClickListener {

                //check if email is empty
                if (email.text.toString().isNullOrBlank()) {
                    email.error = "Please enter an email"
                    return@setOnClickListener
                } else {

                    loadingIndicator.show()
                    val userDbManagement = UserDbManagement()
                    val auth: FirebaseAuth = FirebaseAuth.getInstance()
                    //check if user exists with email
                    userDbManagement.isUserExistsWithEmail(email.text.toString(), auth) { exists ->
                        if (exists) {


                            //send password reset email
                            auth.sendPasswordResetEmail(email.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                    }
                                }
                        } else {
                            email.error = "User with this email does not exist"
                        }
                    }
                    loadingIndicator.hide()
                }

            }
        }else{
            changePasswordButton.setOnClickListener {
                Toast.makeText(this, "Please connect to the internet", Toast.LENGTH_SHORT).show()
            }
        }

        //get cancel button
        val cancelButton = findViewById<Button>(R.id.backButton)
        //set on click listener to go to login
        cancelButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
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