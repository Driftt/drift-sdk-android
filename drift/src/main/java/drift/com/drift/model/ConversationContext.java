package drift.com.drift.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import drift.com.drift.helpers.LoggerHelper;

/**
 * Created by eoin on 15/08/2017.
 */

public class ConversationContext {

    @Expose
    @SerializedName("userAgent")
    String userAgent;

    public ConversationContext(Context context) {

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;

            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String versionRelease = Build.VERSION.RELEASE;

            userAgent = "Mobile App / " + manufacturer + ": " + model + " / App Version: " + version + " / Android: " + versionRelease;

            LoggerHelper.logMessage("CONTEXT", userAgent);

        } catch (PackageManager.NameNotFoundException e) {
            userAgent = "";
        }
    }
}
