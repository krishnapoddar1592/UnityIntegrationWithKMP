//
//  UnityBridgeProtocol.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


// iosApp/iosApp/Unity/UnityBridgeProtocol.swift
import Foundation

@objc protocol UnityBridgeProtocol {
    func initialize()
    func startParticleAnimation()
    func cleanup()
}
