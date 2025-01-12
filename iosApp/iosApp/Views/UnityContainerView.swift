//
//  UnityContainerView.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


// iosApp/iosApp/Views/UnityContainerView.swift
// iosApp/iosApp/Views/UnityContainerView.swift
import SwiftUI
import shared

struct UnityContainerView: View {
    @StateObject private var viewModel = UnityViewModel()
    
    var body: some View {
        VStack {
            UnityView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .onAppear {
                    viewModel.initialize()
                }
                .onDisappear {
                    viewModel.cleanup()
                }
            
            Button("Start Animation") {
                viewModel.startAnimation()
            }
            .padding()
        }
    }
}

class UnityViewModel: ObservableObject {
    private let unityBridge = PlatformUnityBridge(context: nil)
    
    func initialize() {
        DispatchQueue.main.async {
            self.unityBridge.initialize()
        }
    }
    
    func startAnimation() {
        DispatchQueue.main.async {
            self.unityBridge.startParticleAnimation()
        }
    }
    
    func cleanup() {
        DispatchQueue.main.async {
            self.unityBridge.cleanup()
        }
    }
}
