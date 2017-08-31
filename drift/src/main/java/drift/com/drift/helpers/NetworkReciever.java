package drift.com.drift.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import drift.com.drift.Drift;




public class NetworkReciever extends BroadcastReceiver {

    private static String TAG = NetworkReciever.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        notifyEventBusOfNetworkChange(connected);
    }

    public static boolean isInternetConnected(Context context) {

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connec.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            return wifiConnected || mobileConnected;
        } else {
            return false;
        }
    }

    public void updateNetowrkState(Context context){
        notifyEventBusOfNetworkChange(isInternetConnected(context));
    }

    private void notifyEventBusOfNetworkChange(boolean connected) {
        LoggerHelper.logMessage(TAG, "Network Changed: Connected: " + String.valueOf(connected));

        Intent networkChanged = new Intent(Notification.NETWORK_STATE_CHANGED);
        LocalBroadcastManager.getInstance(Drift.getContext()).sendBroadcast(networkChanged);
    }
}