package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.dbmanagement.GoalManagement
import com.trinityjayd.hourglass.models.Goal

class Goals : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        //initialize auth
        auth = Firebase.auth
        val user = auth.currentUser!!
        val uid = user.uid

        //get min hours edit text
        val minHours = findViewById<EditText>(R.id.minimumGoalEditText)
        //get max hours edit text
        val maxHours = findViewById<EditText>(R.id.maximumGoalEditText)

        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.show()
        //populate edit texts with current goals
        populateGoalTextViews(uid, minHours, maxHours)
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


        //get save button
        val save = findViewById<Button>(R.id.saveButton)
        //set on click listener
        save.setOnClickListener {
            //check if min hours is empty
            if (minHours.text.toString().isNullOrBlank()) {
                minHours.error = "Please enter a minimum goal"
                return@setOnClickListener
            } else if (maxHours.text.toString().isNullOrBlank()) {
                //check if max hours is empty
                maxHours.error = "Please enter a maximum goal"
                return@setOnClickListener
            } else if (minHours.text.toString().toInt() > maxHours.text.toString().toInt()) {
                //check if min hours is greater than max hours
                minHours.error = "Minimum goal cannot be greater than maximum goal"
                return@setOnClickListener
            } else if (minHours.text.toString().toInt() < 0) {
                //check if min hours is less than 0
                minHours.error = "Minimum goal cannot be less than 0"
                return@setOnClickListener
            } else if (maxHours.text.toString().toInt() < 0) {
                //check if max hours is less than 0
                maxHours.error = "Maximum goal cannot be less than 0"
                return@setOnClickListener
            } else if (maxHours.text.toString().toInt() > 24) {
                //check if max hours is greater than 24
                maxHours.error = "Maximum goal cannot be greater than 24"
                return@setOnClickListener
            } else if (minHours.text.toString().toInt() > 24) {
                //check if min hours is greater than 24
                minHours.error = "Minimum goal cannot be greater than 24"
                return@setOnClickListener
            } else {
                loadingIndicator.show()
                //save min hours
                val minHours = minHours.text.toString().toInt()
                //save max hours
                val maxHours = maxHours.text.toString().toInt()

                //get current date
                val currentDate = java.util.Calendar.getInstance().time
                val date = currentDate.toString()

                //create goals object
                val goals = Goal(
                    minHours,
                    maxHours,
                    date,
                    uid
                )

                //add goals to database
                GoalManagement().addGoalToDatabase(goals)

                loadingIndicator.hide()
                //create intent to go to home page
                val intent = Intent(this, Home::class.java)
                //start activity
                startActivity(intent)
            }


        }
    }

    private fun populateGoalTextViews(uid: String, minEditText: EditText, maxEditText: EditText) {
        val database = Firebase.database.reference
        //get the goal where the user id is the current user
        val goalReference = database.child("goals").child(uid)

        goalReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val goal = dataSnapshot.getValue(Goal::class.java)
                    // set the text views to the current goals
                    minEditText.text =
                        Editable.Factory.getInstance().newEditable(goal?.minHours.toString())
                    maxEditText.text =
                        Editable.Factory.getInstance().newEditable(goal?.maxHours.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                return
            }
        })
    }

}