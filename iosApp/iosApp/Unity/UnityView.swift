//
//  UnityView.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import UnityFramework
import UIKit

struct UnityView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let hostingController = UIViewController()

        if let unityView = UnityFrameworkWrapper.shared.getUnityView() {
            unityView.frame = hostingController.view.bounds
            unityView.autoresizingMask = [UIView.AutoresizingMask.flexibleWidth, UIView.AutoresizingMask.flexibleHeight]
            hostingController.view.addSubview(unityView)
        }

        return hostingController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
