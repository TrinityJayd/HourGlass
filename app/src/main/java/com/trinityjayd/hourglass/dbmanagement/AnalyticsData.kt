package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Entry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsData {

    private var database = Firebase.database.reference
    private var auth = Firebase.auth
    private lateinit var startDate : Date
    private lateinit var endDate : Date

    fun getGoals () : Pair<Double, Double> {
        val uid = auth.currentUser?.uid

        var minimumGoal = 0.0
        var maximumGoal = 0.0

        //get minimum and maximum goal from database
        val goalRef = database.child("goals").child(uid.toString())
        goalRef.get().addOnSuccessListener {
            minimumGoal = it.child("minimumGoal").value.toString().toDouble()
            maximumGoal = it.child("maximumGoal").value.toString().toDouble()
        }

        return Pair(minimumGoal, maximumGoal)
    }

    fun hoursPerDay (start : String, end : String) : ArrayList<Double> {
        var userEntries = listOf<Entry>()
        val entryManagement = EntryManagement()

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        if (start == "Start" && end == "End"){
            //get current date
            val currentDate = calendar.time
            val dates = getWeekDates(currentDate)

            startDate = dates.first
            endDate = dates.second
        }else{
            startDate = dateFormat.parse(start)
            endDate = dateFormat.parse(end)
        }

        val uid = auth.currentUser?.uid

        entryManagement.filterEntries(
            uid.toString(),
            "All",
            startDate.toString(),
            endDate.toString()
        ) { entries ->
            //update entries in adapter
            userEntries = entries

        }

        calendar.time = startDate
        var hoursPerDay = ArrayList<Double>()

        while (calendar.time.before(endDate)) {
            var totalHours = 0.0
            for (entry in userEntries){
                val entryDate = dateFormat.parse(entry.date)
                if (calendar.time == entryDate){
                    totalHours += entry.hours
                }
            }
            hoursPerDay.add(totalHours)
            calendar.add(Calendar.DATE, 1)
        }

        return hoursPerDay


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

    fun getDayNames (dates : Pair<Date, Date>) : ArrayList<String>{
        val calendar = Calendar.getInstance()
        calendar.time = dates.first

        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        var dayNames = ArrayList<String>()

        while (calendar.time.before(dates.second)) {
            val dayName = dateFormat.format(calendar.time)
            dayNames.add(dayName)
            calendar.add(Calendar.DATE, 1)
        }

        return dayNames
    }




}