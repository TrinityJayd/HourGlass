package com.trinityjayd.hourglass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.UserDbManagement
import com.trinityjayd.hourglass.models.User

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth


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

        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.hide()


        val loginButton = findViewById<Button>(R.id.loginButton)
        val googleSignInButton = findViewById<Button>(R.id.googleLoginButton)

        if(isInternetAvailable()){
            //set on click listener to home activity
            loginButton.setOnClickListener {
                //get email and password
                val email = findViewById<EditText>(R.id.editTextEmailAddress)

                val validationMethods = ValidationMethods()

                //check if email and password are empty
                if (email.text.toString().isNullOrBlank()) {
                    email.error = "Please enter an email."
                    return@setOnClickListener
                } else if (password.text.toString().isNullOrBlank()) {
                    password.error = "Please enter a password."
                    return@setOnClickListener
                } else if (!validationMethods.isEmailValid(email.text.toString())) {
                    //check if email is valid
                    email.error = "Please enter a valid email."
                    return@setOnClickListener
                } else {
                    val emailText = email.text.toString()
                    val passwordText = password.text.toString()
                    val userDbManagement = UserDbManagement()


                    loadingIndicator.show()
                    //check if user exists in database
                    userDbManagement.isUserExistsWithEmail(emailText) { exists ->
                        if (exists) {

                            userDbManagement.isGoogleAccountExists(emailText) { hasGoogleAccount ->
                                if (hasGoogleAccount) {
                                    Toast.makeText(this, "Please login with Google.", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    //sign in user
                                    auth.signInWithEmailAndPassword(emailText, passwordText)
                                        .addOnCompleteListener(this) { task ->
                                            if (task.isSuccessful) {
                                                val intent = Intent(this, Home::class.java)
                                                startActivity(intent)
                                                loadingIndicator.hide()

                                            } else {
                                                Toast.makeText(
                                                    baseContext,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT,
                                                ).show()

                                            }
                                        }
                                }
                            }

                        } else {
                            email.error = "User with this email does not exist"
                        }
                    }
                    loadingIndicator.hide()

                }

            }


            googleSignInButton.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build()

                googleSignInClient = GoogleSignIn.getClient(this, gso)

                loadingIndicator.show()
                signInWithGoogle()
                loadingIndicator.hide()
            }

        }else{
            loginButton.setOnClickListener{
                Toast.makeText(this, "Please connect to the internet to login.", Toast.LENGTH_SHORT).show()
            }

            googleSignInButton.setOnClickListener{
                Toast.makeText(this, "Please connect to the internet to login.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Code Attribution
    //Author: Develop Ideas
    //Date: 7 June 2023
    //Link: https://www.youtube.com/watch?v=vDmabKQ0T9s

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result

            val userDbManagement = UserDbManagement()
            if (account != null) {
                userDbManagement.isUserExistsWithEmail(account.email!!) { exists ->
                    if (exists) {
                        userDbManagement.isGoogleAccountExists(account.email!!) { exists ->
                            if (exists) {
                                updateUI(account)
                            } else {
                                Toast.makeText(this, "Please use Email/Password login", Toast.LENGTH_SHORT)
                                    .show()
                                googleSignInClient.signOut()
                            }
                        }
                    } else {
                        updateUI(account)
                    }
                }



            }
        } else {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
        }

    }


    private fun updateUI(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val displayName = account?.displayName
                    val uid = auth.currentUser?.uid

                    val userDbManagement = UserDbManagement()

                    userDbManagement.isUserExistsWithUid(uid!!) { exists ->
                        if (!exists) {
                            //create user object
                            val newUser = User(uid, displayName!!)

                            userDbManagement.addUserToDatabase(newUser)
                        }
                    }
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
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