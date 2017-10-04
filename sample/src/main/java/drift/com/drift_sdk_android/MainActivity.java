package drift.com.drift_sdk_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import drift.com.drift.Drift;

public class MainActivity extends AppCompatActivity {

    Button conversationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.drift_sdk_toolbar);
        setSupportActionBar(toolbar);

//        Drift.registerUser("6399", "eoin@8bytes.ie");

        Drift.registerUser("6399", "eoin@8bytes.ie");




        conversationButton = findViewById(R.id.drift_sdk_main_activity_show_conversations);

        conversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drift.showConversationActivity();
            }
        });

    }

}
