package com.trinityjayd.hourglass.dbmanagement

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.trinityjayd.hourglass.models.Entry

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

}