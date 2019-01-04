package drift.com.drift.views

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent

import androidx.appcompat.app.AlertDialog
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import drift.com.drift.R
import drift.com.drift.activities.ImageViewerActivity
import drift.com.drift.helpers.ColorHelper
import drift.com.drift.helpers.DownloadHelper
import drift.com.drift.model.Attachment
import drift.com.drift.model.Message

/**
 * Created by eoin on 26/08/2017.
 */

class AttachmentView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var fileExtensionTextView: TextView
    private var fileNameTextView: TextView
    private var fileSizeTextView: TextView
    private var attachmentIconImageView: ImageView
    private lateinit var activity: Activity

    constructor(activity: Activity) : this(activity, null) {
        this.activity = activity
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.drift_sdk_attachment_view, this)
        fileExtensionTextView = findViewById(R.id.drift_sdk_attachment_view_extension_text_view)
        fileNameTextView = findViewById(R.id.drift_sdk_attachment_view_file_name_text_view)
        fileSizeTextView = findViewById(R.id.drift_sdk_attachment_view_file_size_text_view)
        attachmentIconImageView = findViewById(R.id.drift_sdk_attachment_view_icon_image_view)
    }

    fun setupForAttachment(attachment: Attachment, message: Message) {

        fileNameTextView.text = attachment.fileName


        attachmentIconImageView.setBackgroundColor(ColorHelper.backgroundColor)

        try {
            val filenameArray = attachment.fileName!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val extension = filenameArray[filenameArray.size - 1]
            fileExtensionTextView.text = extension
        } catch (t: Throwable) {
            t.printStackTrace()
            fileExtensionTextView.text = "N/A"
        }

        fileSizeTextView.text = Formatter.formatShortFileSize(context, attachment.size!!.toLong())

        setOnClickListener {
            if (attachment.isImage) {

                val intent = ImageViewerActivity.intentForUri(activity, attachment.url)
                activity.startActivity(intent)

            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(attachment.fileName)
                        .setMessage("Download File?")
                        .setPositiveButton("Download") { dialog, id -> DownloadHelper.downloadAttachment(context, attachment) }
                        .setNeutralButton("Cancel") { dialog, id -> }

                builder.create().show()
            }
        }

    }

}
