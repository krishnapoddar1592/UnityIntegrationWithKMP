//
//  ContentView.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        NavigationView {
            VStack {
                NavigationLink("Launch Unity Demo") {
                    UnityContainerView()
                }
                .padding()
            }
        }
    }
}
