package drift.com.drift.helpers

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast

import java.util.ArrayList

import drift.com.drift.model.Attachment
import drift.com.drift.model.Auth

import android.content.Context.DOWNLOAD_SERVICE


class DownloadHelper {

    private val downloads = ArrayList<Long>()

    fun recordDownload(dowloadId: Long) {
        downloads.add(dowloadId)
    }


    fun isDownloadFromApp(downloadId: Long): Boolean {
        return downloads.contains(downloadId)
    }

    companion object {

        private var _downloadHelper: DownloadHelper? = null

        val instance: DownloadHelper
            get() {
                if (_downloadHelper == null) {
                    _downloadHelper = DownloadHelper()
                }

                return _downloadHelper
            }

        fun downloadAttachment(context: Context, attachment: Attachment) {
            val imageUri = Uri.parse(attachment.url)

            downloadUri(context, imageUri, attachment.fileName)
        }

        fun downloadUri(context: Context, uri: Uri, fileName: String?) {
            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(uri)

            val auth = Auth.instance

            if (auth != null) {
                request.addRequestHeader("Authorization", "bearer " + auth!!.accessToken)
            }

            request.setTitle(fileName)

            request.setDescription(fileName)
            request.allowScanningByMediaScanner()
            request.setDestinationInExternalFilesDir(context,
                    Environment.DIRECTORY_DOWNLOADS, fileName)

            //Enqueue download and save into referenceId
            DownloadHelper.instance.recordDownload(downloadManager.enqueue(request))
            Toast.makeText(context, "Download Starting", Toast.LENGTH_LONG).show()
        }
    }


}
