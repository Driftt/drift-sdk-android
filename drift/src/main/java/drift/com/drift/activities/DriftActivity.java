package drift.com.drift.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import drift.com.drift.helpers.NetworkReciever;
import drift.com.drift.helpers.Notification;

public class DriftActivity extends AppCompatActivity {

    private NetworkReciever networkChangeReciever = new NetworkReciever();

    private BroadcastReceiver networkChanged;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkChanged = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                networkDidUpdate();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReciever, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(networkChanged, new IntentFilter(Notification.NETWORK_STATE_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReciever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChanged);
    }


    @Override
    protected void onResume() {
        super.onResume();
        networkChangeReciever.updateNetowrkState(this);
    }

    private void networkDidUpdate(){
        if (NetworkReciever.isInternetConnected(this)) {
            refreshData();
        } else {
            networkNotAvailable();
        }
    }

    public void refreshData() {

    }

    public void networkNotAvailable() {

    }
}
