//
//  UnityBridge.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


// iosApp/iosApp/Unity/UnityBridge.swift
import Foundation
import shared

class UnityBridge: NSObject, UnityBridgeProtocol {
    private let wrapper = UnityFrameworkWrapper.shared
    
    func initialize() {
        wrapper.initialize()
    }
    
    func startParticleAnimation() {
        wrapper.sendMessage("ParticleSystem", method: "StartAnimation", message: "")
    }
    
    func cleanup() {
        wrapper.cleanup()
    }
}
