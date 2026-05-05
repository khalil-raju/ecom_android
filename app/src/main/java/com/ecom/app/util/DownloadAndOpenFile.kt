package com.ecom.app.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

fun downloadFile(
    context: Context,
    url: String,
    fileName: String? = null,
    title: String = "Download"
): Long {
    val uri = url.toUri()
    val resolvedFileName = fileName ?: uri.lastPathSegment ?: "download"

    val mimeType = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
        ?: "application/octet-stream"

    val request = DownloadManager.Request(uri).apply {
        setTitle(title)
        setDescription("")
        setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        setAllowedOverMetered(true)
        setAllowedOverRoaming(true)
        setMimeType(mimeType)

        setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            resolvedFileName
        )
    }

    val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()

    return downloadManager.enqueue(request)
}

fun openFileUrl(
    context: Context,
    uri: Uri,
    mimeType: String = "application/pdf"
) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
    }
}

fun downloadFileAndOpen(
    context: Context,
    url: String,
    fileName: String? = null,
    title: String = "Download",
    mimeType: String = "application/pdf"
) {
    val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val downloadId = downloadFile(
        context = context,
        url = url,
        fileName = fileName,
        title = title
    )

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val completedId = intent?.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID,
                -1L
            )

            if (completedId != downloadId) return

            try {
                val uri = downloadManager.getUriForDownloadedFile(downloadId)

                if (uri != null) {
                    openFileUrl(
                        context = context,
                        uri = uri,
                        mimeType = mimeType
                    )
                }
            } finally {
                context.unregisterReceiver(this)
            }
        }
    }

    ContextCompat.registerReceiver(
        context,
        receiver,
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
        ContextCompat.RECEIVER_NOT_EXPORTED
    )
}