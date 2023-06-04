package com.trinityjayd.hourglass.models

data class Entry(
    val taskName: String = "",
    val category: String = "",
    val date: String = "",
    val hours: Int = 0,
    val minutes: Int = 0,
    val taskDescription: String = "",
    val imageKey: String? = null,
    val uid: String = ""
)
