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

        val home = findViewById<ImageView>(R.id.homeImageView)
        home.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val totalTime = findViewById<LinearLayout>(R.id.totalTimeLayout)
        totalTime.setOnClickListener {
            val intent = Intent(this, TotalHours::class.java)
            startActivity(intent)
        }

        val hoursPerDay = findViewById<LinearLayout>(R.id.workHoursLayout)
        hoursPerDay.setOnClickListener {
            val intent = Intent(this, HoursPerDay::class.java)
            startActivity(intent)
        }

        val goalProgress = findViewById<LinearLayout>(R.id.monthlyGoalLayout)
        goalProgress.setOnClickListener {
            val intent = Intent(this, GoalProgress::class.java)
            startActivity(intent)
        }


    }
}