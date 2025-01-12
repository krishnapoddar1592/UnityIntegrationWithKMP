//
//  UnityView.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import UnityFramework

struct UnityView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let hostingController = UIViewController()

        if let unityView = UnityFrameworkWrapper.shared.unityFramework?.appController()?.rootView {
            unityView.frame = hostingController.view.bounds
            unityView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            hostingController.view.addSubview(unityView)
        }

        return hostingController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
