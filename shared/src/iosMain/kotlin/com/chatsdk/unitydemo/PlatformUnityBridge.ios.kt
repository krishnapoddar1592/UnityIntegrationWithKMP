package com.chatsdk.unitydemo

//import platform.Foundation.NSObject

//actual class PlatformUnityBridge actual constructor(context: Any?) : UnityBridge {
//    private val unityWrapper: UnityFrameworkWrapper = UnityFrameworkWrapper.shared()
//
//    override fun initialize() {
//        unityWrapper.initialize()
//    }
//
//    override fun startParticleAnimation() {
//        unityWrapper.sendMessage("ParticleSystem", "StartAnimation", "")
//    }
//
//    override fun cleanup() {
//        unityWrapper.cleanup()
//    }
//}
//package com.chatsdk.unitydemo

import platform.Foundation.*
import platform.UIKit.*
import kotlinx.cinterop.*

//actual class PlatformUnityBridge actual constructor(context: Any?) : UnityBridge {
//    private val unityWrapper = UnityFrameworkWrapper.shared()
//
//    override fun initialize() {
//        unityWrapper.initialize()
//    }
//
//    override fun startParticleAnimation() {
//        unityWrapper.sendMessage("ParticleSystem", "StartAnimation", "")
//    }
//    override fun cleanup() {
//        unityWrapper.cleanup()
//    }
//
//}

//package com.chatsdk.unitydemo
//
actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
    override fun initialize() {
        // TODO: Implement once UnityFrameworkWrapper is created
        println("Unity initialize called on iOS")
    }

    override fun startParticleAnimation() {
        // TODO: Implement once UnityFrameworkWrapper is created
        println("Start animation called on iOS")
    }

    override fun cleanup() {
        // TODO: Implement once UnityFrameworkWrapper is created
        println("Cleanup called on iOS")
    }
}