//
//  UnityUtils.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation

enum UnityUtils {
    static func handleUnityMessage(_ message: String) {
        // Handle callbacks from Unity here
        print("Received message from Unity: \(message)")
    }
}
