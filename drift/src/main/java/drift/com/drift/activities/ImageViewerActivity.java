package drift.com.drift.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import drift.com.drift.R;
import drift.com.drift.helpers.DownloadHelper;
import drift.com.drift.helpers.StatusBarColorizer;

public class ImageViewerActivity extends DriftActivity {

    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;
    private static String IMAGE_URI = "DRIFT_IMAGE_URI";

    public static Intent intentForUri(Context context, Uri imageUri) {

        Bundle data = new Bundle();
        data.putString(IMAGE_URI, imageUri.toString());

        return new Intent(context, ImageViewerActivity.class)
                .putExtras(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drift_sdk_activity_image_viewer);
        StatusBarColorizer.setActivityColor(this);
        getSupportActionBar().setTitle("Attachment");

        imageView = findViewById(R.id.drift_sdk_image_viewer_image_view);
        progressBar = findViewById(R.id.drift_sdk_image_viewer_progress_view);

        Intent intent = getIntent();

        if ( intent.getExtras() != null ) {
            String imageUriAsString = intent.getExtras().getString(IMAGE_URI);

            if (imageUriAsString == null || imageUriAsString.isEmpty()) {
                finish();
                return;
            }

            imageUri = Uri.parse(imageUriAsString);
        }

        Glide.with(this)
                .load(imageUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ImageViewerActivity.this, "Failed to load Image", Toast.LENGTH_LONG).show();
                        finish();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drift_sdk_image_attachment_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.drift_sdk_image_attachment_download) {

            DownloadHelper.downloadUri(this, imageUri, "DriftImageDownload");


            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
