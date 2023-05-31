package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class NewCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_category)

        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, MainActivity::class.java)
            //start activity
            startActivity(intent)
        }

        //get save button
        val save = findViewById<Button>(R.id.saveButton)
        //set on click listener
        save.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, MainActivity::class.java)
            //start activity
            startActivity(intent)
        }
    }
}