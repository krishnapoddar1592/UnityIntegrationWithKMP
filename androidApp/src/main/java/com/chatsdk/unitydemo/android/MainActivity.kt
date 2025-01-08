package com.chatsdk.unitydemo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chatsdk.unitydemo.Greeting

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.chatsdk.unitydemo.PlatformUnityBridge
import com.unity3d.player.UnityPlayer

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