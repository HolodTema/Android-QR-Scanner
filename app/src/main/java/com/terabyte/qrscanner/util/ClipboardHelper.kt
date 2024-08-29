package com.terabyte.qrscanner.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import com.terabyte.qrscanner.CLIPBOARD_MANAGER_LABEL
import timber.log.Timber

object ClipboardHelper {

    fun copyToClipboard(context: Context, stringToCopy: String, listener: () -> Unit) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (URLUtil.isValidUrl(stringToCopy)) {
            try {
                val uri = Uri.parse(stringToCopy)
                val clipData =
                    ClipData.newUri(context.contentResolver, CLIPBOARD_MANAGER_LABEL, uri)
                clipboardManager.setPrimaryClip(clipData)
            } catch (t: Throwable) {
                // TODO: log timber better
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

}