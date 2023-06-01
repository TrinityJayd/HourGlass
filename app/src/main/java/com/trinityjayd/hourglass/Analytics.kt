package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout

class Analytics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home activity
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //get total time layout
        val totalTime = findViewById<LinearLayout>(R.id.totalTimeLayout)
        //set on click listener
        totalTime.setOnClickListener {
            val intent = Intent(this, TotalHours::class.java)
            startActivity(intent)
        }


    }
}