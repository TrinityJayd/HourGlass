package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.CategoryManagement
import com.trinityjayd.hourglass.dbmanagement.EntryManagement
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TotalHours : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var category: Spinner
    private lateinit var startDate: Button
    private lateinit var endDate: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_hours)

        //get auth instance
        auth = Firebase.auth
        val user = auth.currentUser!!
        val uid = user.uid

        //get start date
        startDate = findViewById(R.id.startDateButton)
        //get end date
        endDate = findViewById(R.id.endDateButton)
        //get category
        category = findViewById(R.id.spinnerCategory)

        populateCategories()
        dateButtons()


        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.hide()

        //get filter button
        val filterButton = findViewById<Button>(R.id.filterButton)

        //set on click listener
        filterButton.setOnClickListener {
            //check if category is selected
            if(category.selectedItem.toString() == "Category"){
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (startDate.text.toString() != "Start" && endDate.text.toString() != "End") {
                //save start date and end date
                val startDateText = startDate.text.toString()
                val endDateText = endDate.text.toString()

                // Format the date strings into Date objects
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDateObj = dateFormat.parse(startDateText)
                val endDateObj = dateFormat.parse(endDateText)

                // Check if the start date is before the end date
                if (startDateObj != null) {
                    if (startDateObj.after(endDateObj)) {
                        startDate.error = "Start date must be before end date"
                        endDate.error = "End date must be after start date"
                        return@setOnClickListener
                    }
                }
            }
            //set error to null
            startDate.error = null
            endDate.error = null

            val entryManagement = EntryManagement()

            loadingIndicator.show()
            //get total time by category
            entryManagement.calculateTotalTimeByCategory(
                uid,
                category.selectedItem.toString(),
                startDate.text.toString(),
                endDate.text.toString()
            ) { totalTimeByCategory ->
                for ((category, totalTime) in totalTimeByCategory) {
                    // Get the total time in hours and minutes
                    val hours = totalTime.first
                    val minutes = totalTime.second

                    // Find the TextView components by their IDs
                    val categoryTextView = findViewById<TextView>(R.id.categoryTextView)
                    val categoryColorView = findViewById<View>(R.id.categoryColorView)
                    val hoursTextView = findViewById<TextView>(R.id.hoursTextView)
                    val minutesTextView = findViewById<TextView>(R.id.minutesTextView)

                    // Set the text values
                    categoryTextView.text = category

                    // Set the background color of the category color view
                    CategoryManagement().getCategoryColor(category) { color ->
                        categoryColorView.setBackgroundColor(color)
                    }

                    // Set the hours and minutes text views
                    hoursTextView.text = hours.toString() + " hour/s"
                    minutesTextView.text = minutes.toString() + " minute/s"

                }
            }
            loadingIndicator.hide()

            //reset the filter text
            startDate.text = "Start"
            endDate.text = "End"
            category.setSelection(0)

        }


        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }


    }


    private fun populateCategories() {
        //get the category names from the children of the logged in user's uid from the realtime database where the user id is the same as the current user
        val user = auth.currentUser!!
        val uid = user.uid
        val database = Firebase.database
        //create array list of categories
        val categories = ArrayList<String>()

        categories.add("Category")
        val myRef = database.getReference("categories/$uid")
        
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

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
                    this@TotalHours, R.layout.spinner_list, categories
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


    private fun dateButtons() {
        //Code Attribution
        //Author: Coding Demos
        //Date: 22/02/2018
        //https://www.codingdemos.com/android-datepicker-button/
        val startDate = findViewById<Button>(R.id.startDateButton)
        startDate.setOnClickListener(View.OnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@TotalHours, { datePicker, year, month, day ->
                    startDate.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })

        val endDate = findViewById<Button>(R.id.endDateButton)
        endDate.setOnClickListener(View.OnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@TotalHours, { datePicker, year, month, day ->
                    endDate.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })
    }
}