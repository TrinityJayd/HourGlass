package com.trinityjayd.hourglass.dbmanagement

import android.app.Application
import com.google.firebase.database.FirebaseDatabase


class Utils : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}