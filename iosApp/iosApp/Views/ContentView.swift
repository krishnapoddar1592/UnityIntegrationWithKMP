//
//  ContentView.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


// iosApp/iosApp/Views/ContentView.swift
//import SwiftUI
//import UIKit
//
//struct ContentView: View {
//    var body: some View {
//        VStack {
//            Text("Unity Integration")
//                .font(.title)
//                .padding()
//            
//            Button("Launch Unity View") {
//                // Present the Unity view controller
//                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
//                   let rootViewController = windowScene.windows.first?.rootViewController {
//                    let unityVC = UnityViewController()
//                    rootViewController.present(unityVC, animated: true)
//                }
//            }
//            .padding()
//            .background(Color.blue)
//            .foregroundColor(.white)
//            .cornerRadius(10)
//        }
//    }
//}
//
//// UIViewControllerRepresentable wrapper if you want to embed Unity directly in SwiftUI
//struct UnityViewRepresentable: UIViewControllerRepresentable {
//    func makeUIViewController(context: Context) -> UnityViewController {
//        return UnityViewController()
//    }
//    
//    func updateUIViewController(_ uiViewController: UnityViewController, context: Context) {
//        // Updates can be handled here if needed
//    }
//}
//
//#Preview {
//    ContentView()
//}
// iosApp/iosApp/Views/ContentView.swift
import SwiftUI
import UIKit
import os.log

// Create a dedicated logger
private let logger = OSLog(subsystem: "com.chatsdk.unitydemo", category: "ThreadDebug")

struct ContentView: View {
    
    var body: some View {
        VStack {
            Text("Unity Integration")
                .font(.title)
                .padding()
            
            Button("Launch Unity View") {
                os_log("Button tapped on thread: %{public}@", log: logger, type: .debug, Thread.current.description)
                
                // Present the Unity view controller
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                   let rootViewController = windowScene.windows.first?.rootViewController {
                    os_log("About to create UnityViewController", log: logger, type: .debug)
                    let unityVC = UnityViewController()
                    os_log("About to present UnityViewController", log: logger, type: .debug)
                    rootViewController.present(unityVC, animated: true)
                }
            }
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(10)
        }
        .onAppear {
            os_log("ContentView appeared on thread: %{public}@", log: logger, type: .debug, Thread.current.description)
        }
    }
}

// UIViewControllerRepresentable wrapper if you want to embed Unity directly in SwiftUI
struct UnityViewRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UnityViewController {
        os_log("makeUIViewController called on thread: %{public}@", log: logger, type: .debug, Thread.current.description)
        return UnityViewController()
    }
    
    func updateUIViewController(_ uiViewController: UnityViewController, context: Context) {
        os_log("updateUIViewController called on thread: %{public}@", log: logger, type: .debug, Thread.current.description)
        // Updates can be handled here if needed
    }
}


