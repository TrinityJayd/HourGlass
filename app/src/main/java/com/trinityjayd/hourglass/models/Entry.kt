package com.trinityjayd.hourglass.models

import java.sql.Time
import java.util.Date

data class Entry(
    val taskName: String,
    val category: String,
    val date: String,
    val hours: Int,
    val minutes: Int,
    val taskDescription: String,
    val uid: String
)