package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //get newEntry layout
        val newEntry = findViewById<LinearLayout>(R.id.newEntryButton)
        //set onClickListener
        newEntry.setOnClickListener {
            //start newEntry activity
            startActivity(Intent(this, NewEntry::class.java))
        }

        //get viewEntries layout
        val yourEntries = findViewById<LinearLayout>(R.id.yourEntriesButton)
        //set onClickListener
        yourEntries.setOnClickListener {
            //start viewEntries activity
            startActivity(Intent(this, ListEntries::class.java))
        }

        //get goals layout
        val goals = findViewById<LinearLayout>(R.id.goalsButton)
        //set onClickListener
        goals.setOnClickListener {
            //start goals activity
            startActivity(Intent(this, Goals::class.java))
        }

        //get analytics layout
        val analytics = findViewById<LinearLayout>(R.id.analyticsButton)
        //set onClickListener
        analytics.setOnClickListener {
            //start analytics activity
            startActivity(Intent(this, Analytics::class.java))
        }

        //get timer layout
        val timer = findViewById<LinearLayout>(R.id.timerLayout)
        //set onClickListener
        timer.setOnClickListener {
            //start timer activity
            startActivity(Intent(this, Timer::class.java))
        }
    }
}