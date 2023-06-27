package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.EntryManagement
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListEntries : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_entries)

        auth = Firebase.auth
        val user = auth.currentUser!!
        uid = user.uid

        populateCategories()
        dateButtons()

        //get recycler view
        val entryRecyclerView = findViewById<RecyclerView>(R.id.entriesRecyclerView)
        entryRecyclerView.layoutManager = LinearLayoutManager(this)

        val entryAdapter = EntryAdapter(emptyList()) // Create an empty adapter
        entryRecyclerView.adapter = entryAdapter

        val entryManagement = EntryManagement()

        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.show()
        //get all entries for the logged in user
        entryManagement.getAllEntriesForUser(uid) { entries ->
            entryAdapter.updateEntries(entries)
        }
        loadingIndicator.hide()

        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        //get filter button
        val filter = findViewById<Button>(R.id.filterButton)
        //set on click listener
        filter.setOnClickListener {
            //get start date
            val startDate = findViewById<Button>(R.id.startDateButton)
            //get end date
            val endDate = findViewById<Button>(R.id.endDateButton)
            //get category
            val category = findViewById<Spinner>(R.id.spinnerCategory)

            //check if start date and end date are not the default values
            if (startDate.text.toString() != "Start" && endDate.text.toString() != "End") {
                val startDateText = startDate.text.toString()
                val endDateText = endDate.text.toString()

                //format the dates
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDateObj = dateFormat.parse(startDateText)
                val endDateObj = dateFormat.parse(endDateText)

                //check if start date is after end date
                if (startDateObj != null) {
                    if (startDateObj.after(endDateObj)) {
                        startDate.error = "Start date must be before end date"
                        endDate.error = "End date must be after start date"
                        return@setOnClickListener
                    }
                }
            }
            //remove errors
            startDate.error = null
            endDate.error = null

            loadingIndicator.show()
            val entryManagement = EntryManagement()

            //filter entries
            entryManagement.filterEntries(
                uid,
                category.selectedItem.toString(),
                startDate.text.toString(),
                endDate.text.toString()
            ) { entries ->
                //update entries in adapter
                entryAdapter.updateEntries(entries)

            }
            loadingIndicator.hide()

            //reset filter text
            startDate.text = "Start"
            endDate.text = "End"
            category.setSelection(0)
        }


    }

    private fun populateCategories() {
        //get the category names from the children of the logged in user's uid from the realtime database where the user id is the same as the current user
        val database = Firebase.database
        val myRef = database.getReference("categories/$uid")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //create array list of categories
                val categories = ArrayList<String>()
                //add select category to array list
                categories.add("All")
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
                    this@ListEntries, R.layout.spinner_list, categories
                )
                //set drop down view resource
                adapter.setDropDownViewResource(R.layout.spinner_list)
                //set adapter
                spinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    //code start date and end date button
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
                this@ListEntries, { datePicker, year, month, day ->
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
                this@ListEntries, { datePicker, year, month, day ->
                    endDate.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })
    }


}


