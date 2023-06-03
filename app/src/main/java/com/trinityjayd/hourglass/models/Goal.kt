package com.trinityjayd.hourglass.models

data class Goal (
    val minHours : Int,
    val maxHours : Int,
    val date : String,
    val uid : String
)