package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Entry

class EntryManagement {
    private var database = Firebase.database.reference


    fun addEntryToDatabase(entry : Entry){
        var entryKey = database.push().key // Generate a unique key for the entry
        if (entryKey != null) {
            database.child("entries").child(entry.uid).child(entryKey!!).setValue(entry)
        }
    }





}