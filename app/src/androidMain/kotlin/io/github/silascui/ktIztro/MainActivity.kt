package io.github.silascui.ktIztro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.silascui.ktIztro.ai.service.PlatformSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformSettings.appContext = applicationContext
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
