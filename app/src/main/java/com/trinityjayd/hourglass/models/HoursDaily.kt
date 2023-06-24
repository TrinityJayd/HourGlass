package com.trinityjayd.hourglass.models

data class HoursDaily (
    val minimumGoal : Double = 0.0,
    val maximumGoal : Double = 0.0,
    val hours : ArrayList<Double> = ArrayList<Double>()
)