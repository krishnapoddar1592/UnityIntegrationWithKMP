//
//  UnityFrameworkWrapper.swift
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import UnityFramework


class UnityFrameworkWrapper {
    static let shared = UnityFrameworkWrapper()
    private var unityFramework: UnityFramework?

    private func getUnityFramework() -> UnityFramework? {
        let bundlePath = Bundle.main.bundlePath
        let frameworkPath = bundlePath + "/Frameworks/UnityFramework.framework"
        let bundle = Bundle(path: frameworkPath)
        if bundle?.isLoaded == false {
            bundle?.load()
        }

        let frameworkClass = NSClassFromString("UnityFramework") as? UnityFramework.Type
        return frameworkClass?.getInstance()
    }

    func initialize() {
        if unityFramework != nil {
            return
        }

        unityFramework = getUnityFramework()
        unityFramework?.setDataBundleId("com.chatsdk.unitydemo")
        unityFramework?.register(self)
        unityFramework?.runEmbedded()
    }

    func sendMessage(_ gameObject: String, _ method: String, _ message: String) {
        unityFramework?.sendMessageToGO(
            withName: gameObject,
            functionName: method,
            message: message
        )
    }

    func cleanup() {
        unityFramework?.unloadApplication()
        unityFramework = nil
    }
}


