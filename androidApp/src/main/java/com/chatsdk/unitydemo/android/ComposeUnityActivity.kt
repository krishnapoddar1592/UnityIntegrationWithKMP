package com.chatsdk.unitydemo.android

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ComposeUnityActivity : ComponentActivity() {
    private var unityWrapper: UnityWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                UnityView(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxWidth(),
                    onUnityPlayerCreated = { wrapper ->
                        unityWrapper = wrapper
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            unityWrapper?.sendMessage(
                                "ParticleSystem",
                                "StartAnimation",
                                ""
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Animation")
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        unityWrapper?.onConfigurationChanged(newConfig)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        unityWrapper?.onWindowFocusChanged(hasFocus)
    }

    override fun onDestroy() {
        super.onDestroy()
        unityWrapper = null
    }
}