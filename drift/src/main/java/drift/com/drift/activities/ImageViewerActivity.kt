package drift.com.drift.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import drift.com.drift.R
import drift.com.drift.helpers.DownloadHelper
import drift.com.drift.helpers.GlideHelper
import drift.com.drift.helpers.StatusBarColorizer

internal class ImageViewerActivity : DriftActivity() {

    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var imageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drift_sdk_activity_image_viewer)
        StatusBarColorizer.setActivityColor(this)
        supportActionBar!!.title = "Attachment"

        imageView = findViewById(R.id.drift_sdk_image_viewer_image_view)
        progressBar = findViewById(R.id.drift_sdk_image_viewer_progress_view)

        val intent = intent

        if (intent.extras != null) {
            val imageUriAsString = intent.extras!!.getString(IMAGE_URI)

            if (imageUriAsString == null || imageUriAsString.isEmpty()) {
                finish()
                return
            }

            imageUri = imageUriAsString
        }

        Glide.with(this)
                .load(GlideHelper.getAttachmentURLForGlide(imageUri))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, o: Any, target: Target<Drawable>, b: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@ImageViewerActivity, "Failed to load Image", Toast.LENGTH_LONG).show()
                        finish()
                        return false
                    }

                    override fun onResourceReady(drawable: Drawable, o: Any, target: Target<Drawable>, dataSource: DataSource, b: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(imageView)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.drift_sdk_image_attachment_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.drift_sdk_image_attachment_download) {

            DownloadHelper.downloadUri(this, Uri.parse(imageUri), "DriftImageDownload")

            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val IMAGE_URI = "DRIFT_IMAGE_URI"

        fun intentForUri(context: Context, imageUri: String): Intent {

            val data = Bundle()
            data.putString(IMAGE_URI, imageUri)

            return Intent(context, ImageViewerActivity::class.java)
                    .putExtras(data)
        }
    }
}
