package com.chatsdk.unitydemo.android

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.unity3d.player.IUnityPlayerLifecycleEvents
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerForActivityOrService
class UnityWrapper(
    private val activity: Activity,
    private val lifecycleEvents: IUnityPlayerLifecycleEvents
) {
    private var unityPlayer: UnityPlayerForActivityOrService? = null

    init {
        try {
            unityPlayer = UnityPlayerForActivityOrService(activity, lifecycleEvents).apply {
                frameLayout.requestFocus()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val view: FrameLayout? get() = unityPlayer?.frameLayout

    fun destroy() {
        unityPlayer?.destroy()
        unityPlayer = null
    }

    fun onPause() {
        unityPlayer?.onPause()
    }

    fun onResume() {
        unityPlayer?.onResume()
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        unityPlayer?.configurationChanged(newConfig)
    }

    fun onWindowFocusChanged(hasFocus: Boolean) {
        unityPlayer?.windowFocusChanged(hasFocus)
    }

    fun sendMessage(gameObject: String, methodName: String, parameter: String = "") {
        try {
            UnityPlayer.UnitySendMessage(gameObject, methodName, parameter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun UnityView(
    modifier: Modifier = Modifier,
    onUnityPlayerCreated: (UnityWrapper) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val unityWrapper = remember(context) {
        UnityWrapper(context as Activity, object : IUnityPlayerLifecycleEvents {
            override fun onUnityPlayerUnloaded() {}
            override fun onUnityPlayerQuitted() {}
        })
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> unityWrapper.onPause()
                Lifecycle.Event.ON_RESUME -> unityWrapper.onResume()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onUnityPlayerCreated(unityWrapper)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            unityWrapper.destroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { _ ->
            unityWrapper.view?.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            } ?: FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { view ->
            // Handle any view updates if needed
        }
    )
}
