package com.trinityjayd.hourglass

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.CategoryManagement
import com.trinityjayd.hourglass.models.Category
import yuku.ambilwarna.AmbilWarnaDialog

class NewCategory : AppCompatActivity() {

    private var pickColor: Button? = null
    private var defaultColor = 0
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_category)

        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.hide()

        //get save button
        val save = findViewById<Button>(R.id.saveButton)

        if(isInternetAvailable()){
            //set on click listener
            save.setOnClickListener {
                //get category name
                val categoryName = findViewById<EditText>(R.id.editTextCategoryName)
                val categoryManagement = CategoryManagement()

                //get current user
                auth = Firebase.auth
                val user = auth.currentUser!!
                val uid = user.uid

                //check if category name is blank
                if (categoryName.text.toString().isNullOrBlank()) {
                    categoryName.error = "Category name cannot be blank"
                    return@setOnClickListener
                } else if (categoryName.text.toString().length > 50) {
                    categoryName.error = "Category name cannot be more than 50 characters"
                    return@setOnClickListener
                } else if (defaultColor == 0) {
                    //check if color is picked
                    pickColor?.error = "Please pick a color"
                    return@setOnClickListener
                } else {
                    loadingIndicator.show()
                    //check if category already exists
                    categoryManagement.isCategoryExists(uid, categoryName.text.toString()) { exists ->
                        if (exists) {
                            categoryName.error = "Category with the same name already exists"
                            loadingIndicator.hide()
                            return@isCategoryExists
                        } else {
                            val category = Category(categoryName.text.toString(), defaultColor, uid)
                            //save category to database
                            categoryManagement.saveCategory(category)

                            //create intent to go to home page
                            val intent = Intent(this, NewEntry::class.java)
                            //start activity
                            startActivity(intent)
                        }
                    }
                    loadingIndicator.hide()

                }
            }
        }else{
            save.setOnClickListener {
                Toast.makeText(this, "Please connect to the internet to add a category.", Toast.LENGTH_SHORT).show()
            }
        }


        //get pick color button
        pickColor = findViewById(R.id.pickColorButton)
        //set on click listener
        pickColor?.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    openColorPickerDialogue()
                }
            })


    }

    //Code Attribution
//Author: GeeksforGeeks
//Link: https://www.geeksforgeeks.org/how-to-create-a-basic-color-picker-tool-in-android/
    fun openColorPickerDialogue() {

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        val colorPickerDialogue = AmbilWarnaDialog(this, defaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // leave this function body as
                    // blank, as the dialog
                    // automatically closes when
                    // clicked on cancel button
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // change the mDefaultColor to
                    // change the GFG text color as
                    // it is returned when the OK
                    // button is clicked from the
                    // color picker dialog
                    defaultColor = color

                    //change the background color of the button
                    pickColor?.setBackgroundColor(defaultColor)
                }
            })
        colorPickerDialogue.show()
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