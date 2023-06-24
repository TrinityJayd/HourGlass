package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Entry
import com.trinityjayd.hourglass.models.HoursDaily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsData {

    private var database = Firebase.database.reference
    private var auth = Firebase.auth

    fun hoursPerDay (startDate : String, endDate : String) : ArrayList<HoursDaily> {
        val data = ArrayList<HoursDaily>()
        val uid = auth.currentUser?.uid


        var minimumGoal = 0.0
        var maximumGoal = 0.0

        //get minimum and maximum goal from database
        val goalRef = database.child("goals").child(uid.toString())
        goalRef.get().addOnSuccessListener {
            minimumGoal = it.child("minimumGoal").value.toString().toDouble()
            maximumGoal = it.child("maximumGoal").value.toString().toDouble()
        }

        var userEntries = listOf<Entry>()
        val entryManagement = EntryManagement()
        if (startDate == "Start" && endDate == "End"){
            //get current date
            val calendar = Calendar.getInstance()
            val currentDate = calendar.time
            val dates = getWeekDates(currentDate)

            entryManagement.filterEntries(
                uid.toString(),
                "All",
                dates.first.toString(),
                dates.second.toString()
            ) { entries ->
                //update entries in adapter
                userEntries = entries
            }

        }else{
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            entryManagement.filterEntries(
                uid.toString(),
                "All",
                start.toString(),
                end.toString()
            ) { entries ->
                //update entries in adapter
                userEntries = entries
            }
        }






        return data


    }

    fun getWeekDates(date: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Set the calendar to the first day of the week (Sunday)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        // Set the time to the beginning of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.time

        // Set the calendar to the last day of the week (Saturday)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
        // Set the time to the end of the day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfWeek = calendar.time

        return Pair(startOfWeek, endOfWeek)
    }




}