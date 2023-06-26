package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsData {

    private var database = Firebase.database.reference
    private var auth = Firebase.auth
    private var dayLabels = ArrayList<String>()


    fun getGoals(callback: (Pair<Float, Float>) -> Unit) {
        val uid = auth.currentUser?.uid

        var minimumGoal: Float
        var maximumGoal: Float

        // get minimum and maximum goal from database
        val goalRef = database.child("goals").child(uid.toString())
        goalRef.get().addOnSuccessListener {
            minimumGoal = it.child("minHours").value.toString().toFloat()
            maximumGoal = it.child("maxHours").value.toString().toFloat()

            // Invoke the callback with the retrieved values
            callback(Pair(minimumGoal, maximumGoal))
        }
    }

    fun getMonthlyGoals(callback: (Pair<Float, Float>) -> Unit) {
        getGoals { goals ->
            val monthlyMinimumGoal = goals.first * 30
            val monthlyMaximumGoal = goals.second * 30

            // Invoke the callback with the monthly goals
            callback(Pair(monthlyMinimumGoal, monthlyMaximumGoal))
        }
    }



    fun hoursPerDay(start: String, end: String, onComplete: (ArrayList<Float>) -> Unit) {
        val entryManagement = EntryManagement()

        val calendar = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val startDate: Date
        val endDate: Date

        //if no values are passed in, get the current week
        if (start == "Start" && end == "End") {
            val currentDate = calendar.time
            val dates = getWeekDates(currentDate)

            //set the start and end dates to the first and last days of the week
            startDate = dates.first
            endDate = dates.second
        } else {
            //otherwise, set the start and end dates to the values passed in
            startDate = dateFormat.parse(start)
            endDate = dateFormat.parse(end)
        }

        val uid = auth.currentUser?.uid

        val formattedStartDate = dateFormat.format(startDate)
        val formattedEndDate = dateFormat.format(endDate)

        entryManagement.filterEntries(
            uid.toString(), "All", formattedStartDate, formattedEndDate
        ) { entries ->
            calendar.time = startDate
            val hoursPerDay = ArrayList<Float>()

            val dates = getDates(Pair(startDate, endDate))
            dayLabels = getDays(dates)

            for (date in dates) {
                var totalMinutes = 0

                for (entry in entries) {
                    val dateFormat2 = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        dateFormat2.parse(date) as Date
                    )

                    val dateCheck = dateFormat.parse(formattedDate)
                    val entryDate = dateFormat.parse(entry.date)

                    if (dateCheck == entryDate) {
                        val entryHours = entry.hours
                        val entryMinutes = entry.minutes
                        val minutes = (entryHours * 60) + entryMinutes
                        totalMinutes += minutes
                    }
                }

                val totalHours = totalMinutes / 60
                val remainingMinutes = totalMinutes % 60


                val timeString = String.format(Locale.getDefault(), "%d.%02d", totalHours, remainingMinutes)
                val totalTimeStr = timeString.toFloat()

                hoursPerDay.add(totalTimeStr)
            }

            onComplete(hoursPerDay)
        }
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

    fun getDates(dates: Pair<Date, Date>): ArrayList<String> {
        val calendar = Calendar.getInstance()
        calendar.time = dates.first

        val dateFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())

        val datesStr = ArrayList<String>()

        while (calendar.time.before(dates.second)) {
            val date = dateFormat.format(calendar.time)
            datesStr.add(date)
            calendar.add(Calendar.DATE, 1)
        }

        return datesStr
    }

    fun getDays(dates: ArrayList<String>): ArrayList<String> {
        val dayNames = ArrayList<String>()
        val dateFormat = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        for (date in dates) {
            val day = dayFormat.format(dateFormat.parse(date))
            dayNames.add(day)
        }

        return dayNames
    }

    fun getDayLabels(): ArrayList<String> {
        return dayLabels
    }

    fun sumHoursArr(time: ArrayList<Float>): String {
        var totalHours = 0
        var totalMinutes = 0

        for (entry in time) {
            val entryHours = entry.toInt()
            val entryMinutes = ((entry - entryHours) * 100).toInt()
            totalHours += entryHours
            totalMinutes += entryMinutes
        }

        totalHours += totalMinutes / 60
        totalMinutes %= 60

        return String.format(Locale.getDefault(), "%d.%02d", totalHours, totalMinutes)

    }



}