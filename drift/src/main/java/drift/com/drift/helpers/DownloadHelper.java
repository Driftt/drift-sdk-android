package drift.com.drift.helpers;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.util.ArrayList;

import drift.com.drift.model.Attachment;
import drift.com.drift.model.Auth;

import static android.content.Context.DOWNLOAD_SERVICE;


public class DownloadHelper {

    private static DownloadHelper _downloadHelper = null;

    private ArrayList<Long> downloads = new ArrayList<>();

    public static DownloadHelper getInstance() {
        if (_downloadHelper == null) {
            _downloadHelper = new DownloadHelper();
        }

        return _downloadHelper;
    }

    public static void downloadAttachment(Context context, Attachment attachment){
        final Uri imageUri = Uri.parse(attachment.getURL());

        downloadUri(context, imageUri, attachment.fileName);
    }

    public static void downloadUri(Context context, Uri uri, String fileName) {
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        Auth auth = Auth.getInstance();

        if (auth != null) {
            request.addRequestHeader("Authorization", "bearer " + auth.getAccessToken());
        }

        request.setTitle(fileName);

        request.setDescription(fileName);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalFilesDir(context,
                Environment.DIRECTORY_DOWNLOADS, fileName);

        //Enqueue download and save into referenceId
        DownloadHelper.getInstance().recordDownload(downloadManager.enqueue(request));
        Toast.makeText(context, "Download Starting", Toast.LENGTH_LONG).show();
    }

    public void recordDownload(long dowloadId){
        downloads.add(dowloadId);
    }


    public boolean isDownloadFromApp(long downloadId){
        return downloads.contains(downloadId);
    }


}
