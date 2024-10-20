package com.soda1127.itbookstorecleanarchitecture

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookStoreApp: Application() {

    override fun onCreate() {
        super.onCreate()

    }

}
