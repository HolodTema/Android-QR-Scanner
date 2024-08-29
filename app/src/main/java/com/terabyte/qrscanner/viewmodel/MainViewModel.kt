package com.terabyte.qrscanner.viewmodel

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.journeyapps.barcodescanner.ScanIntentResult
import com.terabyte.qrscanner.CLIPBOARD_MANAGER_LABEL
import com.terabyte.qrscanner.ROOM_MAX_AMOUNT_QR_INFO_OBJECTS
import com.terabyte.qrscanner.data.QRInfo
import com.terabyte.qrscanner.data.RoomManager
import com.terabyte.qrscanner.util.ClipboardHelper
import com.terabyte.qrscanner.util.ScanHelper
import timber.log.Timber

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    val liveDataToastNothingWasChosenFromGallery = MutableLiveData(false)
    val liveDataToastNothingScannedCamera = MutableLiveData(false)
    val liveDataToastNothingScannedGallery = MutableLiveData(false)


    val currentQRInfo = mutableStateOf(QRInfo.createEmpty())
    val scanHistory = mutableStateOf<List<QRInfo>?>(null)



    init {
        loadScanHistory()
    }



    fun onScannedUsingCamera(result: ScanIntentResult) {
        if (result.contents == null) {
            liveDataToastNothingScannedCamera.value = true
        } else {
            currentQRInfo.value = createQRInfo(result.contents)
        }
    }

    fun onCopyButtonClickedListener(context: Context, stringToCopy: String, listener: () -> Unit) {
        ClipboardHelper.copyToClipboard(context, stringToCopy) {
            listener()
        }
    }

    fun onImagePickedFromGalleryToScan(context: Context, result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            if (result.data == null || result.data?.data == null) {
                liveDataToastNothingWasChosenFromGallery.value = true
            } else {
                scanFromGallery(context, result.data!!.data!!)
            }
        } else {
            liveDataToastNothingWasChosenFromGallery.value = true

        }
    }



    private fun createQRInfo(contents: String): QRInfo {
        val qrInfo = QRInfo(
            id = 0,
            info = contents
        )
        RoomManager.insert(application, qrInfo) {
            if(scanHistory.value!=null && scanHistory.value!!.size>ROOM_MAX_AMOUNT_QR_INFO_OBJECTS) {
                RoomManager.delete(application, scanHistory.value!![scanHistory.value!!.size-1]) {}
            }
            // TODO: log it
        }
        return qrInfo
    }

    private fun scanFromGallery(context: Context, imageUri: Uri) {
        ScanHelper.scanCodeByImageUri(
            context,
            imageUri,
            successListener = {
                currentQRInfo.value = it
            },
            failureListener = {
                liveDataToastNothingScannedGallery.value = true
            }
        )
    }

    private fun loadScanHistory() {
        RoomManager.getAll(application) {
            scanHistory.value = it
        }
    }
}