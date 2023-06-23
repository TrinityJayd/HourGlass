package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //get newEntry layout
        val newEntry = findViewById<CardView>(R.id.newEntryButton)
        //set onClickListener
        newEntry.setOnClickListener {
            //start newEntry activity
            startActivity(Intent(this, NewEntry::class.java))
        }

        //get viewEntries layout
        val yourEntries = findViewById<CardView>(R.id.yourEntriesButton)
        //set onClickListener
        yourEntries.setOnClickListener {
            //start viewEntries activity
            startActivity(Intent(this, ListEntries::class.java))
        }

        //get goals layout
        val goals = findViewById<CardView>(R.id.goalsButton)
        //set onClickListener
        goals.setOnClickListener {
            //start goals activity
            startActivity(Intent(this, Goals::class.java))
        }

        //get analytics layout
        val analytics = findViewById<CardView>(R.id.analyticsButton)
        //set onClickListener
        analytics.setOnClickListener {
            //start analytics activity
            startActivity(Intent(this, Analytics::class.java))
        }

        //get timer layout
        val timer = findViewById<CardView>(R.id.timerButton)
        //set onClickListener
        timer.setOnClickListener {
            //start timer activity
            startActivity(Intent(this, Timer::class.java))
        }

        //get settings layout
        val settings = findViewById<CardView>(R.id.settingsButton)
        //set onClickListener
        settings.setOnClickListener {
            //start settings activity
            startActivity(Intent(this, Settings::class.java))
        }

    }
}