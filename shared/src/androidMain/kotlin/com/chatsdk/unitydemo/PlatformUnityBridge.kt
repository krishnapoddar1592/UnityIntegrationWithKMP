package com.chatsdk.unitydemo

import com.chatsdk.unitydemo.UnityBridge

import android.content.Context
import android.content.Intent

actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
    override fun initialize() {
        (context as? Context)?.let { ctx ->
            try {
                val unityActivityClass = Class.forName("com.chatsdk.unitydemo.android.AndroidUnityActivity")
                val intent = Intent(ctx, unityActivityClass as Class<*>)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ctx.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun startParticleAnimation() {
        (context as? Context)?.let { ctx ->
            val intent = Intent("com.chatsdk.unitydemo.START_ANIMATION")
            ctx.sendBroadcast(intent)
        }
    }

    override fun cleanup() {
        (context as? Context)?.let { ctx ->
            val intent = Intent("com.chatsdk.unitydemo.CLEANUP")
            ctx.sendBroadcast(intent)
        }
    }
}