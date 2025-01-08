package com.chatsdk.unitydemo.android

interface UnityInterface {
    fun triggerParticleAnimation()
    fun onAnimationComplete()
    fun onAnimationError(error: Exception)
}