package drift.com.drift_sdk_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Button

import drift.com.drift.Drift

class MainActivity : AppCompatActivity() {

    private lateinit var conversationButton: Button

    private lateinit var createConversationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.drift_sdk_toolbar)
        setSupportActionBar(toolbar)

        Drift.registerUser("", "")

        conversationButton = findViewById(R.id.drift_sdk_main_activity_show_conversations)
        createConversationButton = findViewById(R.id.drift_sdk_main_activity_create_conversations)
        conversationButton.setOnClickListener { Drift.showConversationActivity() }

        createConversationButton.setOnClickListener {
            Drift.showCreateConversationActivity()
        }

    }

}
