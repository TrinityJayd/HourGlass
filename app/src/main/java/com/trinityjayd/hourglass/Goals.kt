package com.trinityjayd.hourglass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

        auth = Firebase.auth
        //get min hours edit text
        val minHours = findViewById<EditText>(R.id.minimumGoalEditText)
        //get max hours edit text
        val maxHours = findViewById<EditText>(R.id.maximumGoalEditText)

        populateGoalTextViews(auth.currentUser!!.uid, minHours, maxHours)

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

    private fun populateGoalTextViews(uid : String, minEditText: EditText, maxEditText : EditText) {
        val database = Firebase.database.reference
        //get the goal where the user id is the current user
        val goalReference = database.child("goals").child(uid)


        goalReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val goal = dataSnapshot.getValue(Goal::class.java)
                    // Assuming you have TextViews for displaying the minimum and maximum hours
                    minEditText.text = Editable.Factory.getInstance().newEditable(goal?.minHours.toString())
                    maxEditText.text = Editable.Factory.getInstance().newEditable(goal?.maxHours.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                return
            }
        })
    }

}