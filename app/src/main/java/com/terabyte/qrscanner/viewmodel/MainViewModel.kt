package com.terabyte.qrscanner.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.terabyte.qrscanner.CLIPBOARD_MANAGER_LABEL
import com.terabyte.qrscanner.data.QRInfo
import com.terabyte.qrscanner.util.ScanHelper
import timber.log.Timber

class MainViewModel: ViewModel() {
    val liveDataToastNothingScannedCamera = MutableLiveData(false)
    val liveDataToastNothingScannedGallery = MutableLiveData(false)


    val currentQRInfo = mutableStateOf<QRInfo>(QRInfo.createEmpty())

    val scanHistory = mutableStateOf<List<QRInfo>?>(null)



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

    fun onCopyButtonClickedListener(context: Context, stringToCopy: String, listener: () -> Unit) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if(URLUtil.isValidUrl(stringToCopy)) {
            try {
                val uri = Uri.parse(stringToCopy)
                val clipData = ClipData.newUri(context.contentResolver, CLIPBOARD_MANAGER_LABEL, uri)
                clipboardManager.setPrimaryClip(clipData)
            }
            catch(t: Throwable) {
                //if cannot parse to uri, copy like an ordinary string
                val clipData = ClipData.newPlainText(CLIPBOARD_MANAGER_LABEL, stringToCopy)
                clipboardManager.setPrimaryClip(clipData)
                Timber.w("When copy button clicked: URLUtil recognized the text as uri, but Uri.parse() not working. Copied as plain text.")
            }
        }
        else {
            //if the qr info is not uri:
            val clipData = ClipData.newPlainText(CLIPBOARD_MANAGER_LABEL, stringToCopy)
            clipboardManager.setPrimaryClip(clipData)
        }
        //listener to show toast with text kinda "Copied!" in MainActivity
        listener()
    }


    private fun createQRInfo(contents: String): QRInfo {
        return QRInfo(
            id = 0,
            info = contents
        )
    }

    fun onImagePickedFromGalleryToScan(context: Context, imageUri: Uri) {
        ScanHelper.scanCodeByImageUri(
            context,
            imageUri,
            successListener = {
                currentQRInfo.value = it
            },
            failureListener = {

            }
        )
    }

}