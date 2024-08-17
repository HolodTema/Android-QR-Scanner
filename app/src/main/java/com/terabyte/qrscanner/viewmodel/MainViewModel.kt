package com.terabyte.qrscanner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.terabyte.qrscanner.data.QRInfo

class MainViewModel: ViewModel() {
    val liveDataToastNothingScannedCamera = MutableLiveData(false)



    val currentQRInfo = mutableStateOf<QRInfo>(QRInfo.createEmpty())


    fun onScannedUsingCamera(result: ScanIntentResult) {
        if(result.contents==null) {
            liveDataToastNothingScannedCamera.value = true
        }
        else {
            currentQRInfo.value = createQRInfo(result.contents)
        }
    }

    fun getScanOptions(): ScanOptions {
        return ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan a QR code")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
        }
    }


    private fun createQRInfo(contents: String): QRInfo {
        return QRInfo(
            id = 0,
            info = contents
        )
    }

}