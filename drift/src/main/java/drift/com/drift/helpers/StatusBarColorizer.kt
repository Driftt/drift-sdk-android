package drift.com.drift.helpers

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity


internal object StatusBarColorizer {

    fun setActivityColor(activity: AppCompatActivity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = ColorHelper.backgroundColorLightened
        }

        if (activity.supportActionBar != null) {
            activity.supportActionBar!!.setBackgroundDrawable(ColorDrawable(ColorHelper.backgroundColor))
        }

    }
}
