package com.trinityjayd.hourglass.models

data class Entry(
    val taskName: String,
    val category: String,
    val date: String,
    val hours: Int,
    val minutes: Int,
    val taskDescription: String,
    val imageKey: String?,
    val uid: String
)