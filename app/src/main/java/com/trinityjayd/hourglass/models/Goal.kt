package com.trinityjayd.hourglass.models

data class Goal(
    val minHours: Int = 0,
    val maxHours: Int = 0,
    val date: String = "",
    val uid: String = ""
)