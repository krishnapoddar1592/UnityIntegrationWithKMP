// iosApp/iosApp/Unity/UnityBridgeImplementation.swift
import Foundation
import shared

class UnityBridgeImplementation: NSObject, UnityBridgeProtocol {
    // Change this line to use the shared instance properly
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
