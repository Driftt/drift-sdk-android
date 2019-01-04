package drift.com.drift.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import drift.com.drift.Drift


class NetworkReciever : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val connected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        notifyEventBusOfNetworkChange(connected)
    }

    fun updateNetowrkState(context: Context) {
        notifyEventBusOfNetworkChange(isInternetConnected(context))
    }

    private fun notifyEventBusOfNetworkChange(connected: Boolean) {
        LoggerHelper.logMessage(TAG, "Network Changed: Connected: " + connected.toString())

        val networkChanged = Intent(Notification.NETWORK_STATE_CHANGED)
        LocalBroadcastManager.getInstance(Drift.getContext()).sendBroadcast(networkChanged)
    }

    companion object {

        private val TAG = NetworkReciever::class.java.simpleName

        fun isInternetConnected(context: Context): Boolean {

            val connec = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeInfo = connec.activeNetworkInfo
            if (activeInfo != null && activeInfo.isConnected) {
                val wifiConnected = activeInfo.type == ConnectivityManager.TYPE_WIFI
                val mobileConnected = activeInfo.type == ConnectivityManager.TYPE_MOBILE
                return wifiConnected || mobileConnected
            } else {
                return false
            }
        }
    }
}