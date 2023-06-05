package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class Timer : AppCompatActivity() {

    //Code Attribution
    //Author: GeeksforGeeks
    //Link:https://www.geeksforgeeks.org/how-to-create-a-stopwatch-app-using-android-studio/


    // Use seconds, running and wasRunning respectively
    // to record the number of seconds passed,
    // whether the stopwatch is running and
    // whether the stopwatch was running
    // before the activity was paused.

    // Number of seconds displayed
    // on the stopwatch
    private var seconds = 0

    // Is the stopwatch running?
    private var running = false

    private var wasRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        //get home Image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //go to home page
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }


        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState
                .getInt("seconds")
            running = savedInstanceState
                .getBoolean("running")
            wasRunning = savedInstanceState
                .getBoolean("wasRunning")
        }
        runTimer()
    }


    // Save the state of the stopwatch
    // if it's about to be destroyed.
    override fun onSaveInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState
            .putInt("seconds", seconds)
        savedInstanceState
            .putBoolean("running", running)
        savedInstanceState
            .putBoolean("wasRunning", wasRunning)
    }

    // If the activity is paused,
    // stop the stopwatch.
    override fun onPause() {
        super.onPause()
        wasRunning = running
        running = false
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously
    override fun onResume() {
        super.onResume()
        if (wasRunning) {
            running = true
        }
    }

    // Start the stopwatch running
    // when the Start button is clicked.
    // Below method gets called
    // when the Start button is clicked.
    fun onClickStart(view: View?) {
        running = true
    }

    // Stop the stopwatch running
    // when the Stop button is clicked.
    // Below method gets called
    // when the Stop button is clicked.
    fun onClickStop(view: View?) {
        running = false
    }

    // Reset the stopwatch when
    // the Reset button is clicked.
    // Below method gets called
    // when the Reset button is clicked.
    fun onClickReset(view: View?) {
        running = false
        seconds = 0
    }

    //If the use clicks the save button
    //go to new entry page
    fun onClickSave(view: View?) {
        //get hours
        val hours = seconds / 3600
        //get minutes
        val minutes = seconds % 3600 / 60

        //round off hours to 2 digits
        val hoursString = String.format("%02d", hours)
        //round off minutes to 2 digits
        val minutesString = String.format("%02d", minutes)


        //go to new entry page with intent and pass hours and minutes
        //this will be the duration of the entry
        val intent = Intent(this, NewEntry::class.java).apply {
            putExtra("hours", hoursString)
            putExtra("minutes", minutesString)
        }

        startActivity(intent)
    }

    // Sets the NUmber of seconds on the timer.
    // The runTimer() method uses a Handler
    // to increment the seconds and
    // update the text view.
    private fun runTimer() {

        // Get the text view.
        val timeView = findViewById<TextView>(R.id.time_view)

        // Creates a new Handler
        val handler = Handler()

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                timeView.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }

}