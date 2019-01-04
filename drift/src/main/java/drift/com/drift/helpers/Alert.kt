package drift.com.drift.helpers

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class Alert {
    companion object {

        fun showAlert(activity: Activity?, title: String, message: String, positiveTitle: String, block: Runnable?) {
            if (activity == null || activity.isFinishing) {
                return
            }
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(message)
            builder.setTitle(title)
                    .setPositiveButton(positiveTitle) { _, _ ->
                        block?.run()
                    }
                    .setNegativeButton("Cancel") { _, _ -> }.create().show()
        }
    }

}