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
import com.terabyte.qrscanner.data.QRInfo
import com.terabyte.qrscanner.data.RoomManager
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
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (URLUtil.isValidUrl(stringToCopy)) {
            try {
                val uri = Uri.parse(stringToCopy)
                val clipData =
                    ClipData.newUri(context.contentResolver, CLIPBOARD_MANAGER_LABEL, uri)
                clipboardManager.setPrimaryClip(clipData)
            } catch (t: Throwable) {
                //if cannot parse to uri, copy like an ordinary string
                val clipData = ClipData.newPlainText(CLIPBOARD_MANAGER_LABEL, stringToCopy)
                clipboardManager.setPrimaryClip(clipData)
                Timber.w("When copy button clicked: URLUtil recognized the text as uri, but Uri.parse() not working. Copied as plain text.")
            }
        } else {
            //if the qr info is not uri:
            val clipData = ClipData.newPlainText(CLIPBOARD_MANAGER_LABEL, stringToCopy)
            clipboardManager.setPrimaryClip(clipData)
        }
        //listener to show toast with text kinda "Copied!" in MainActivity
        listener()
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