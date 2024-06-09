package com.example.accessibiltyservice

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var button : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        checkOverlayPermission()
        button.setOnClickListener {

            // Check if the accessibility service is enabled, if not, open accessibility settings
            if (!isAccessibilityServiceEnabled()) {
                openAccessibilitySettings()

                Toast.makeText(this, "service start", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun isAccessibilityServiceEnabled(): Boolean {
            val componentName = ComponentName(this, MyAccessibilityService::class.java)
            val accessibilityServices = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return accessibilityServices?.contains(componentName.flattenToString()) ?: false
        }

        private fun openAccessibilitySettings() {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

      private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(myIntent)
        }
    }

    }