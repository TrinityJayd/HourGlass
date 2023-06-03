package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Goal

class GoalManagement {
    private var database = Firebase.database.reference

    fun addGoalToDatabase(goal: Goal) {
        database.child("goals").child(goal.uid).setValue(goal)

    }

    //update goal
    fun updateGoal(goal: Goal) {
        database.child("goals").child(goal.uid).setValue(goal)
    }

}
