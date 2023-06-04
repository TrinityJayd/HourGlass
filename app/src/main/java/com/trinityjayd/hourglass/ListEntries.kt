package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class ListEntries : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_entries)

        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        //Code Attribution
        //Author: Coding Demos
        //Date: 22/02/2018
        //https://www.codingdemos.com/android-datepicker-button/
        val date = findViewById<Button>(R.id.datePickerButton)
        date.setOnClickListener(View.OnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this@ListEntries,
                { datePicker, year, month, day ->
                    date.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })
    }

    private fun populateCategories(){
        //get the category names from the children of the logged in user's uid from the realtime database where the user id is the same as the current user
        val user = auth.currentUser!!
        val uid = user.uid
        val database = Firebase.database
        val myRef = database.getReference("categories/$uid")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //create array list of categories
                val categories = ArrayList<String>()
                //add select category to array list
                categories.add("Select Category")
                //loop through each category in the database
                for (category in dataSnapshot.children) {
                    //get the category name
                    val categoryName = category.child("name").getValue(String::class.java)
                    //add the category name to the array list
                    categories.add(categoryName!!)
                }
                //get spinner
                val spinner = findViewById<Spinner>(R.id.spinnerCategory)
                //create array adapter
                val adapter = ArrayAdapter(
                    this@ListEntries,
                    R.layout.spinner_list,
                    categories
                )
                //set drop down view resource
                adapter.setDropDownViewResource(R.layout.spinner_list)
                //set adapter
                spinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("NewEntry", "Failed to read value.", error.toException())
            }
        })


    }
}