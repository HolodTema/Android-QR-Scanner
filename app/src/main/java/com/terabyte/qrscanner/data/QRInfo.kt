package com.terabyte.qrscanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Entity(tableName = "QRInfos")
data class QRInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val info: String,
    val date: String = SimpleDateFormat("dd.mm.yyyy - hh:mm", Locale.ROOT).format(Date()),
    val type: String = "QR"
) {
    companion object {
        fun createEmpty(): QRInfo {
            return QRInfo(
                0,
                "",
                "",
                ""
            )
        }
    }
}
