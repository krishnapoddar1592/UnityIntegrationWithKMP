package com.chatsdk.unitydemo

expect class PlatformUnityBridge(context: Any?) : UnityBridge {
    override fun initialize()

    override fun startParticleAnimation()

    override fun cleanup()
}