package drift.com.drift.views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import drift.com.drift.R;
import drift.com.drift.activities.ImageViewerActivity;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.DownloadHelper;
import drift.com.drift.model.Attachment;
import drift.com.drift.model.Message;

/**
 * Created by eoin on 26/08/2017.
 */

public class AttachmentView extends LinearLayout {

    TextView fileExtensionTextView;
    TextView fileNameTextView;
    TextView fileSizeTextView;
    ImageView attachmentIconImageView;
    Activity activity;
    public AttachmentView(Activity activity){
        this(activity, null);
        this.activity = activity;
    }

    public AttachmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.drift_sdk_attachment_view, this);
        fileExtensionTextView = findViewById(R.id.drift_sdk_attachment_view_extension_text_view);
        fileNameTextView = findViewById(R.id.drift_sdk_attachment_view_file_name_text_view);
        fileSizeTextView = findViewById(R.id.drift_sdk_attachment_view_file_size_text_view);
        attachmentIconImageView = findViewById(R.id.drift_sdk_attachment_view_icon_image_view);
    }

    public void setupForAttachment(final Attachment attachment, final Message message){

        fileNameTextView.setText(attachment.fileName);


        attachmentIconImageView.setBackgroundColor(ColorHelper.getBackgroundColor());

        try {
            String filenameArray[] = attachment.fileName.split("\\.");
            String extension = filenameArray[filenameArray.length-1];
            fileExtensionTextView.setText(extension);
        } catch (Throwable t){
            t.printStackTrace();
            fileExtensionTextView.setText("N/A");
        }
        fileSizeTextView.setText(Formatter.formatShortFileSize(getContext(), attachment.size));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attachment.isImage()){

                    Intent intent = ImageViewerActivity.intentForUri(activity, attachment.getURL());
                    activity.startActivity(intent);

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(attachment.fileName)
                            .setMessage("Download File?")
                            .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DownloadHelper.downloadAttachment(getContext(), attachment);
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    builder.create().show();
                }
            }
        });

    }

}
