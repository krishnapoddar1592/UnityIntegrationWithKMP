//
//  UnityContainerView.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import shared

struct UnityContainerView: View {
    private let unityBridge = PlatformUnityBridge(context: nil) // Added 'context:' label
    
    var body: some View {
        VStack {
            UnityView()
                .frame(height: UIScreen.main.bounds.height * 0.6)
            
            Button("Start Animation") {
                unityBridge.startParticleAnimation()
            }
            .padding()
        }
    }
}

