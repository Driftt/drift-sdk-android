package drift.com.drift.helpers;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;


public class StatusBarColorizer {

    public static void setActivityColor(AppCompatActivity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ColorHelper.getBackgroundColorLightened());
        }

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ColorHelper.getBackgroundColor()));
        }

    }
}
