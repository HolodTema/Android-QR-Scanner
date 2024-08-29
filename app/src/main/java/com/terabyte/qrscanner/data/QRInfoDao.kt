package com.terabyte.qrscanner.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QRInfoDao {

    @Insert
    fun insert(qrInfo: QRInfo)

    @Delete
    fun delete(qrInfo: QRInfo)

    @Query("select * from QRInfos")
    fun getAll(): List<QRInfo>
}