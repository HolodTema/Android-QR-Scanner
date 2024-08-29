package com.terabyte.qrscanner.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.terabyte.qrscanner.ROOM_DB_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RoomManager {
    private lateinit var instance: AppDatabase

    private fun getInstance(context: Context): AppDatabase  {
        return if(::instance.isInitialized) instance
        else {
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                ROOM_DB_NAME
            )
                .build()
        }
    }

    fun getAll(context: Context, listener: (List<QRInfo>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getInstance(context).getQRInfoDao().getAll()
            CoroutineScope(Dispatchers.Main).launch {
                //reversed result to make them in decrease. From the recent to ancient by date field.
                listener(result.reversed())
            }
        }
    }

    fun insert(context: Context, qrInfo: QRInfo, listener: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            getInstance(context).getQRInfoDao().insert(qrInfo)
            CoroutineScope(Dispatchers.Main).launch {
                listener()
            }
        }
    }

    fun delete(context: Context, qrInfo: QRInfo, listener: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            getInstance(context).getQRInfoDao().delete(qrInfo)
            CoroutineScope(Dispatchers.Main).launch {
                listener()
            }
        }
    }
}