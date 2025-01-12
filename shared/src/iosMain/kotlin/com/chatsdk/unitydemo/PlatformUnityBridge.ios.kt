// shared/src/iosMain/kotlin/com/chatsdk/unitydemo/PlatformUnityBridge.ios.kt
//package com.chatsdk.unitydemo
//
//import platform.Foundation.*
//import platform.UIKit.*
//
//actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
//    init {
//        println("PlatformUnityBridge initialized")
//    }
//
//    override fun initialize() {
//        println("Unity initialize called on iOS")
//    }
//
//    override fun startParticleAnimation() {
//        println("Start animation called on iOS")
//    }
//
//    override fun cleanup() {
//        println("Cleanup called on iOS")
//    }
//}
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
//actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
//    override fun initialize() {
//        // TODO: Implement once UnityFrameworkWrapper is created
//        println("Unity initialize called on iOS")
//    }
//
//    override fun startParticleAnimation() {
//        // TODO: Implement once UnityFrameworkWrapper is created
//        println("Start animation called on iOS")
//    }
//
//    override fun cleanup() {
//        // TODO: Implement once UnityFrameworkWrapper is created
//        println("Cleanup called on iOS")
//    }
//}
package com.chatsdk.unitydemo

import platform.Foundation.*
import platform.darwin.*
import kotlinx.cinterop.*
//import com.chatsdk.unitydemo.Unity. // This should now be available
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
//    private val unityWrapper = UnityFrameworkWrapper.shared
//    private val unityWrapper = TODO()

    actual override fun initialize() {
        dispatch_async(dispatch_get_main_queue()) {
//            this.unityWrapper.initialize()
        }
    }

    actual override fun startParticleAnimation() {
        dispatch_async(dispatch_get_main_queue()) {
//            this.unityWrapper.sendMessage("ParticleSystem", "StartAnimation", "")
        }
    }

    actual override fun cleanup() {
        dispatch_async(dispatch_get_main_queue()) {
//            this.unityWrapper.cleanup()
        }
    }
}