package com.terabyte.qrscanner.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QRInfoDao {

    @Insert
    fun insert(qrInfo: QRInfo)

    @Query("select * from QRInfos")
    fun getAll(): List<QRInfo>
}