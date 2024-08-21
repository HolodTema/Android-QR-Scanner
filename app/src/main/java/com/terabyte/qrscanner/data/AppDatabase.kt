package com.terabyte.qrscanner.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QRInfo::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getQRInfoDao(): QRInfoDao
}