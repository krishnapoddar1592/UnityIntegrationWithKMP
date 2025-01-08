package com.chatsdk.unitydemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform