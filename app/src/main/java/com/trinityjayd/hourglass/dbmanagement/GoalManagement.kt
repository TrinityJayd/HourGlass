package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Goal

class GoalManagement {
    private var database = Firebase.database.reference

    fun addGoalToDatabase(goal: Goal) {
        //add goal to database
        database.child("goals").child(goal.uid).setValue(goal)

    }


    fun updateGoal(goal: Goal) {
        //update goal
        database.child("goals").child(goal.uid).setValue(goal)
    }

}
