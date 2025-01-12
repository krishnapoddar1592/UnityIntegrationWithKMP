//package com.chatsdk.unitydemo
//
//actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
//    override fun initialize() {
//        // Initialize Unity view controller
//        UnityFrameworkWrapper.initialize()
//    }
//
//    override fun startParticleAnimation() {
//        UnityFrameworkWrapper.sendMessage(
//            "ParticleSystem",  // GameObject name
//            "StartAnimation",  // Method name
//            ""               // Parameters
//        )
//    }
//
//    override fun cleanup() {
//        UnityFrameworkWrapper.cleanup()
//    }
//}
package com.chatsdk.unitydemo

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