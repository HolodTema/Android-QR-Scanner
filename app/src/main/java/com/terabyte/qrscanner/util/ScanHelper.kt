package com.terabyte.qrscanner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.MixedDecoder
import com.terabyte.qrscanner.data.QRInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ScanHelper {

    fun scanCodeByImageUri(context: Context, imageUri: Uri, successListener: (QRInfo) -> Unit, failureListener: () -> Unit) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val image = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
                    }

                    val intArray = IntArray(image.width * image.height)
                    image.getPixels(intArray, 0, image.width, 0, 0, image.width, image.height)

                    val source = RGBLuminanceSource(image.width, image.height, intArray)
                    val reader = MixedDecoder(MultiFormatReader())
                    val result = reader.decode(source)

                    val qrInfo = QRInfo(
                        id = 0,
                        info = result.text
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        successListener(qrInfo)
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        failureListener()
                    }
                }
            }
    }
}