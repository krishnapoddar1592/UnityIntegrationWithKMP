package com.chatsdk.unitydemo.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.player.UnityPlayerActivity
import com.unity3d.player.UnityPlayerForActivityOrService
import com.unity3d.player.UnityPlayerGameActivity



//class AndroidUnityActivity : UnityPlayerActivity() {
//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when (intent?.action) {
//                "com.chatsdk.unitydemo.START_ANIMATION" -> triggerParticleAnimation()
//                "com.chatsdk.unitydemo.CLEANUP" -> finish()
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        registerReceiver(receiver, IntentFilter().apply {
//            addAction("com.chatsdk.unitydemo.START_ANIMATION")
//            addAction("com.chatsdk.unitydemo.CLEANUP")
//        }, RECEIVER_NOT_EXPORTED)
//    }
//
//    override fun onDestroy() {
//        unregisterReceiver(receiver)
//        super.onDestroy()
//    }
//
//    private fun triggerParticleAnimation() {
//        mUnityPlayer?.let { player ->
//            player.getFrameLayout().post {
//                player.getFrameLayout().javaClass.getMethod("UnitySendMessage", String::class.java, String::class.java, String::class.java)
//                    .invoke(player.getFrameLayout(), "ParticleSystem", "StartParticleAnimation", "")
//            }
//        }
//    }
//}

// androidApp/src/main/kotlin/com/example/unitykmp/android/AndroidUnityActivity.kt


// androidApp/src/main/kotlin/com/example/unitykmp/android/AndroidUnityActivity.kt


// androidApp/src/main/kotlin/com/example/unitykmp/android/AndroidUnityActivity.kt
// androidApp/src/main/kotlin/com/example/unitykmp/android/AndroidUnityActivity.kt

import com.unity3d.player.UnityPlayer

class AndroidUnityActivity : UnityPlayerActivity() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.chatsdk.unitydemo.START_ANIMATION" -> triggerParticleAnimation()
                "com.chatsdk.unitydemo.CLEANUP" -> finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register broadcast receiver
        registerReceiver(receiver, IntentFilter().apply {
            addAction("com.chatsdk.unitydemo.START_ANIMATION")
            addAction("com.chatsdk.unitydemo.CLEANUP")
        }, RECEIVER_NOT_EXPORTED)

        // Create a vertical LinearLayout as main container
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Get Unity's frame layout
        val unityFrame = mUnityPlayer.getFrameLayout()
        (unityFrame.parent as? ViewGroup)?.removeView(unityFrame)

        // Set Unity frame to take 60% height
        unityFrame.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            weight = 0.6f
        }

        // Create button container for the bottom 40%
        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            ).apply {
                weight = 0.4f
            }
            setBackgroundColor(android.graphics.Color.WHITE)
        }

        // Create and add button
        val startButton = Button(this).apply {
            text = "Start Animation"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 32, 32, 32)
            }
            setOnClickListener {
                triggerParticleAnimation()
            }
        }

        // Add views to their containers
        buttonContainer.addView(startButton)
        mainLayout.addView(unityFrame)
        mainLayout.addView(buttonContainer)

        // Set the main layout as content
        setContentView(mainLayout)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun triggerParticleAnimation() {
        try {
            // Using UnityPlayer directly to send message
            UnityPlayer.UnitySendMessage(
                "ParticleSystem",
                "StartAnimation",
                ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}