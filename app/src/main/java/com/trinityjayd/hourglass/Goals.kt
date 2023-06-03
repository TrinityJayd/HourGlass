package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.GoalManagement
import com.trinityjayd.hourglass.models.Goal

class Goals : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        //get save button
        val save = findViewById<Button>(R.id.saveButton)
        //set on click listener
        save.setOnClickListener {
            //get min hours edit text
            val minHours = findViewById<EditText>(R.id.minimumGoalEditText)
            //get max hours edit text
            val maxHours = findViewById<EditText>(R.id.maximumGoalEditText)

            if (minHours.text.toString().isNullOrBlank()) {
                minHours.error = "Please enter a minimum goal"
                return@setOnClickListener
            } else if (maxHours.text.toString().isNullOrBlank()) {
                maxHours.error = "Please enter a maximum goal"
                return@setOnClickListener
            } else if (minHours.text.toString().toInt() > maxHours.text.toString().toInt()) {
                minHours.error = "Minimum goal cannot be greater than maximum goal"
                return@setOnClickListener
            } else if (minHours.text.toString().toInt() < 0) {
                minHours.error = "Minimum goal cannot be less than 0"
                return@setOnClickListener
            } else if (maxHours.text.toString().toInt() < 0) {
                maxHours.error = "Maximum goal cannot be less than 0"
                return@setOnClickListener
            } else if (maxHours.text.toString().toInt() > 24) {
                maxHours.error = "Maximum goal cannot be greater than 24"
                return@setOnClickListener
            } else if (minHours.text.toString().toInt() > 24) {
                minHours.error = "Minimum goal cannot be greater than 24"
                return@setOnClickListener
            } else {
                //save min hours
                val minHours = minHours.text.toString().toInt()
                //save max hours
                val maxHours = maxHours.text.toString().toInt()

                val currentDate = java.util.Calendar.getInstance().time
                val date = currentDate.toString()

                auth = Firebase.auth
                val user = auth.currentUser!!
                val uid = user.uid

                //create goals object
                val goals = Goal(
                    minHours,
                    maxHours,
                    date,
                    uid
                )

                GoalManagement().addGoalToDatabase(goals)

                //create intent to go to home page
                val intent = Intent(this, Home::class.java)
                //start activity
                startActivity(intent)
            }


        }
    }
}