package com.chatsdk.unitydemo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chatsdk.unitydemo.PlatformUnityBridge

class MainActivity : ComponentActivity() {
    private lateinit var unityBridge: PlatformUnityBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unityBridge = PlatformUnityBridge(this)

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { unityBridge.initialize() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Launch Unity")
                }

                Button(
                    onClick = { unityBridge.startParticleAnimation() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Start Animation")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unityBridge.cleanup()
    }
}