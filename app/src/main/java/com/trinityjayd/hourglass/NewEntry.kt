package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar


class NewEntry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)

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

        //get date button
        val date = findViewById<Button>(R.id.datePickerButton)
        date.setOnClickListener(View.OnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this@NewEntry,
                { datePicker, year, month, day ->
                    date.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })


    }
}