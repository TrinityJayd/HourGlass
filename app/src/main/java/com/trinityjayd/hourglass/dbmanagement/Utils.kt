package com.trinityjayd.hourglass.dbmanagement

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Utils : Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable disk persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        var database = Firebase.database.reference
        database.keepSynced(true)
    }
}