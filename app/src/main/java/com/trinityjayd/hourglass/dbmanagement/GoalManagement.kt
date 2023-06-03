package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Goal

class GoalManagement {
    private var database = Firebase.database.reference

    fun addGoalToDatabase(goal: Goal) {
        val goalKey = database.push().key // Generate a unique key for the entry
        if (goalKey != null) {
            database.child("goals").child(goalKey).setValue(goal)
        }
    }
}