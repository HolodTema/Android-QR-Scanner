package com.terabyte.qrscanner.data

import java.text.SimpleDateFormat
import java.util.Date

data class QRInfo(
    val id: Int,
    val info: String,
    val date: String = SimpleDateFormat("dd.mm.yyyy - hh:mm").format(Date()),
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
