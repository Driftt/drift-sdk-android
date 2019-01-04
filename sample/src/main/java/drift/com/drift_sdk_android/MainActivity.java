package drift.com.drift_sdk_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import drift.com.drift.Drift;

public class MainActivity extends AppCompatActivity {

    Button conversationButton;

    Button createConversationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.drift_sdk_toolbar);
        setSupportActionBar(toolbar);



        Drift.registerUser("", "");
        conversationButton = findViewById(R.id.drift_sdk_main_activity_show_conversations);
        createConversationButton = findViewById(R.id.drift_sdk_main_activity_create_conversations);
        conversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drift.showConversationActivity();
            }
        });

        createConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drift.showCreateConversationActivity();
            }
        });

    }

}
