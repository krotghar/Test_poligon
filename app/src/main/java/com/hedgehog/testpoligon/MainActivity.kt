package com.hedgehog.testpoligon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.text)
        val btn = findViewById<Button>(R.id.event)

        firebaseAnalytics = Firebase.analytics

        btn.setOnClickListener {
            firebaseAnalytics.logEvent("test", null)
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }

        val map: Map<String, Any> = HashMap()

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(map)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    text.text = remoteConfig["test"].asString()
                }
            }

        FirebaseInstallations.getInstance().getToken(/* forceRefresh */ true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation auth token: " + task.result?.token)
                } else {
                    Log.e("Installations", "Unable to get Installation auth token")
                }
            }

    }
}