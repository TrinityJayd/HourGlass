package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar


class NewEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)

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

            val datePickerDialog = DatePickerDialog(
                this@NewEntry,
                { datePicker, year, month, day ->
                    date.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })

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
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        //Code Attribution
        //Author: Geeks for Geeks
        //Link: https://www.geeksforgeeks.org/spinner-in-kotlin/
        val languages = arrayOf("Java", "Kotlin", "Python", "C#", "JavaScript")
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_list, languages
            )
            spinner.adapter = adapter


            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Toast.makeText(
                        this@NewEntry,
                        "Selected" + " " +
                                "" + languages[position], Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


    }
}