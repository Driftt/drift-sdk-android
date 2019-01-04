package drift.com.drift.model

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import drift.com.drift.helpers.LoggerHelper

/**
 * Created by eoin on 15/08/2017.
 */

class ConversationContext(context: Context) {

    @Expose
    @SerializedName("userAgent")
    internal var userAgent: String

    init {

        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName

            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val versionRelease = Build.VERSION.RELEASE

            userAgent = "Mobile App / $manufacturer: $model / App Version: $version / Android: $versionRelease"

            LoggerHelper.logMessage("CONTEXT", userAgent)

        } catch (e: PackageManager.NameNotFoundException) {
            userAgent = ""
        }

    }
}
