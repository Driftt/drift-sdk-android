package drift.com.drift.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import drift.com.drift.helpers.NetworkReciever
import drift.com.drift.helpers.Notification

internal open class DriftActivity : AppCompatActivity() {

    private val networkChangeReciever = NetworkReciever()

    private var networkChanged: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkChanged = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                networkDidUpdate()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReciever, filter)
        LocalBroadcastManager.getInstance(this).registerReceiver(networkChanged!!, IntentFilter(Notification.NETWORK_STATE_CHANGED))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkChangeReciever)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChanged!!)
    }


    override fun onResume() {
        super.onResume()
        networkChangeReciever.updateNetworkState(this)
    }

    private fun networkDidUpdate() {
        if (NetworkReciever.isInternetConnected(this)) {
            refreshData()
        } else {
            networkNotAvailable()
        }
    }

    open fun refreshData() {

    }

    open fun networkNotAvailable() {

    }
}
