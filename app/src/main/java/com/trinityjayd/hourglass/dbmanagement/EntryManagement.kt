package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Entry
import java.text.SimpleDateFormat
import java.util.Locale

class EntryManagement {
    private var database = Firebase.database.reference


    fun addEntryToDatabase(entry: Entry) {
        var entryKey = database.push().key // Generate a unique key for the entry
        if (entryKey != null) {
            database.child("entries").child(entry.uid).child(entryKey!!).setValue(entry)
        }
    }

    //get all entries for a user
    fun getAllEntriesForUser(uid: String, callback: (List<Entry>) -> Unit) {
        database.child("entries").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val entries = mutableListOf<Entry>()
                    for (ds in dataSnapshot.children) {
                        val entry = ds.getValue(Entry::class.java)
                        entry?.let {
                            entries.add(it)
                        }
                    }
                    callback(entries)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error, if needed
                    callback(emptyList())
                }
            })
    }


    fun filterEntries(uid: String, categoryName: String, startDate: String, endDate: String, callback: (List<Entry>) -> Unit) {
        getAllEntriesForUser(uid) { entries ->
            val filteredEntries = entries.filter { entry ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val entryDate = dateFormat.parse(entry.date)
                var isCategoryMatch = true
                var isStartDateMatch = true
                var isEndDateMatch = true

                if (categoryName != "All" && entry.category != categoryName && categoryName != "Category") {
                    isCategoryMatch = false // Filter out entries with category not matching the specified name
                }

                if (startDate != "Start Date") {
                    val startDateObj = dateFormat.parse(startDate)
                    isStartDateMatch = entryDate?.after(startDateObj) ?: false || entryDate?.equals(startDateObj) ?: false // Filter entries after the start date
                }

                if (endDate != "End Date") {
                    val endDateObj = dateFormat.parse(endDate)
                    isEndDateMatch = entryDate?.before(endDateObj) ?: false || entryDate?.equals(endDateObj) ?: false// Filter entries before the end date
                }

                isCategoryMatch && isStartDateMatch && isEndDateMatch
            }
            callback(filteredEntries)
        }
    }

    fun calculateTotalTimeByCategory(uid: String, categoryName: String, startDate: String, endDate: String, callback: (Map<String, Pair<Int, Int>>) -> Unit) {
        filterEntries(uid, categoryName, startDate, endDate) { filteredEntries ->
            val totalTimeByCategory = mutableMapOf<String, Pair<Int, Int>>()

            for (entry in filteredEntries) {
                val category = entry.category
                var categoryColor = 0
                CategoryManagement().getCategoryColor(entry.category) { color ->
                    categoryColor = color
                }
                val hours = entry.hours
                val minutes = entry.minutes

                val existingTotal = totalTimeByCategory[category]
                if (existingTotal != null) {
                    val (existingHours, existingMinutes) = existingTotal
                    val newHours = existingHours + hours
                    val newMinutes = existingMinutes + minutes
                    totalTimeByCategory[category] = Pair(newHours, newMinutes)
                } else {
                    totalTimeByCategory[category] = Pair(hours, minutes)
                }
            }

            callback(totalTimeByCategory)
        }
    }








}