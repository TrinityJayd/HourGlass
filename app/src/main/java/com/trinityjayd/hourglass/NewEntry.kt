package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.EntryManagement
import com.trinityjayd.hourglass.models.Entry
import java.util.Calendar


class NewEntry : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)

        auth = Firebase.auth

        populateCategories()
        createDatePickerDialog()


        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        //get new category button
        val newCategory = findViewById<Button>(R.id.newCategoryButton)
        //set on click listener
        newCategory.setOnClickListener {
            //create intent to go to new category page
            val intent = Intent(this, NewCategory::class.java)
            //start activity
            startActivity(intent)
        }

        //get save button
        val save = findViewById<Button>(R.id.saveButton)
        //set on click listener
        save.setOnClickListener {

            //create object of validation methods
            val validationMethods = ValidationMethods()

            //get task name edit test
            val taskName = findViewById<EditText>(R.id.editTextTaskName)
            val category = findViewById<Spinner>(R.id.spinnerCategory)
            val date = findViewById<Button>(R.id.datePickerButton)
            val hours = findViewById<EditText>(R.id.hoursEditText)
            val minutes = findViewById<EditText>(R.id.minutesEditText)
            val description = findViewById<EditText>(R.id.editTextNotes)

            if (taskName.text.toString().isNullOrBlank()) {
                taskName.error = "Please enter a task name."
                return@setOnClickListener
            } else if (taskName.text.toString().length > 50) {
                taskName.error = "Please enter a task name less than 50 characters."
                return@setOnClickListener
            } else if (category.selectedItem.toString() == "Select Category") {
                //display error message
                Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (date.text.toString().isNullOrBlank()) {
                date.error = "Please select a date."
                return@setOnClickListener
            } else if (hours.text.toString().isNullOrBlank()) {
                hours.error = "Please enter hours."
                return@setOnClickListener
            } else if (hours.text.toString().toInt() > 24) {
                hours.error = "Please enter hours less than 24."
                return@setOnClickListener
            } else if (hours.text.toString().toInt() < 0) {
                hours.error = "Please enter hours greater than 0."
                return@setOnClickListener
            } else if (minutes.text.toString().isNullOrBlank()) {
                minutes.error = "Please enter minutes."
                return@setOnClickListener
            } else if (minutes.text.toString().toInt() > 60) {
                minutes.error = "Please enter minutes less than 60."
                return@setOnClickListener
            } else if (minutes.text.toString().toInt() < 0) {
                minutes.error = "Please enter minutes greater than 0."
                return@setOnClickListener
            } else if (description.text.toString().isNullOrBlank()) {
                //set description to blank
                description.setText("")
            } else if (description.text.toString().length > 100) {
                description.error = "Please enter a description less than 1000 characters."
                return@setOnClickListener
            } else {
                val taskNameText = taskName.text.toString()
                val selectedCategory = category.selectedItem.toString()
                val selectedDate = date.text.toString()
                val hoursText = hours.text.toString()
                val hoursInt = hoursText.toInt()
                val minutesText = minutes.text.toString()
                val minutesInt = minutesText.toInt()
                val descriptionText = description.text.toString()


                val user = auth.currentUser!!
                val uid = user.uid

                //create entry object
                val entry = Entry(
                    taskNameText,
                    selectedCategory,
                    selectedDate,
                    hoursInt,
                    minutesInt,
                    descriptionText,
                    uid
                )

                EntryManagement().addEntryToDatabase(entry)

                //create intent to go to home page
                val intent = Intent(this, Home::class.java)
                //start activity
                startActivity(intent)
            }


        }



    }

    private fun createDatePickerDialog() {
        //Code Attribution
        //Author: Coding Demos
        //Date: 22/02/2018
        //https://www.codingdemos.com/android-datepicker-button/
        val date = findViewById<Button>(R.id.datePickerButton)
        date.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@NewEntry,
                { datePicker, year, month, day ->
                    date.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        }

    }

    //create function that populates the spinner with the categories from the database
    private fun populateCategories(){
        //get the categories from the realtime database where the user id is the same as the current user
        val database = Firebase.database
        val myRef = database.getReference("categories")
        val user = auth.currentUser!!
        val uid = user.uid
        val query = myRef.orderByChild("uid").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //create array list of categories
                val categories = ArrayList<String>()
                //add select category to array list
                categories.add("Select Category")
                //loop through each category
                for (ds in dataSnapshot.children) {
                    //get category name
                    val categoryName = ds.child("name").getValue(String::class.java)
                    //add category name to array list
                    categories.add(categoryName!!)
                }
                //get spinner
                val spinner = findViewById<Spinner>(R.id.spinnerCategory)
                //create array adapter
                val adapter = ArrayAdapter(
                    this@NewEntry,
                    R.layout.spinner_list, categories
                )
                //set adapter
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        //set variable to selected item
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException())
            }
        })

    }



}