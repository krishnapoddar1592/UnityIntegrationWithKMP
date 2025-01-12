// iosApp/iosApp/Unity/UnityFrameworkWrapper.swift
import Foundation
import UIKit

// Forward declare UnityFramework class and protocol
@objc protocol UnityFrameworkProtocol {
    func setDataBundleId(_ bundleId: String)
    func register(_ delegate: UnityFrameworkListener)
    func runEmbedded(withArgc argc: Int32, argv: UnsafeMutablePointer<UnsafeMutablePointer<Int8>?>?, appLaunchOpts: [AnyHashable: Any]?)
    func unloadApplication()
    func appController() -> UnityAppController?
    func sendMessageToGO(withName goName: String, functionName: String, message: String)
}

@objc protocol UnityFrameworkListener {
    @objc optional func unityDidUnload(_ notification: Notification!)
    @objc optional func unityDidQuit(_ notification: Notification!)
}

@objc class UnityAppController: NSObject {
    @objc var rootView: UIView? { nil }
}

@objc class UnityFrameworkWrapper: NSObject, UnityFrameworkListener {
    @objc static let shared = UnityFrameworkWrapper()
    private var unityFramework: UnityFrameworkProtocol?
    
    private override init() {
        super.init()
    }
    
    @objc func unityDidUnload(_ notification: Notification!) {
        // Handle Unity unload
    }
    
    @objc func unityDidQuit(_ notification: Notification!) {
        // Handle Unity quit
    }
    
    @objc func initialize() {
        if unityFramework != nil {
            return
        }
        unityFramework = getUnityFramework()
        unityFramework?.setDataBundleId("com.chatsdk.unitydemo")
        unityFramework?.register(self)
        
        let argc = CommandLine.argc
        let argv = CommandLine.unsafeArgv
        unityFramework?.runEmbedded(withArgc: Int32(argc), argv: argv, appLaunchOpts: nil)
    }
    
    private func getUnityFramework() -> UnityFrameworkProtocol? {
        let bundlePath = Bundle.main.bundlePath
        let frameworkPath = bundlePath + "/Frameworks/UnityFramework.framework"
        let bundle = Bundle(path: frameworkPath)
        if bundle?.isLoaded == false {
            bundle?.load()
        }
        
        let frameworkClassName = "UnityFramework"
        let frameworkClass = NSClassFromString(frameworkClassName)
        
        // Get the Unity Framework instance using runtime invocation
        let selector = NSSelectorFromString("getInstance")
        let getInstance = frameworkClass?.method(for: selector)
        
        if let getInstanceIMP = getInstance {
            typealias GetInstanceFunction = @convention(c) (AnyClass, Selector) -> AnyObject?
            let getInstanceFunc = unsafeBitCast(getInstanceIMP, to: GetInstanceFunction.self)
            if let instance = getInstanceFunc(frameworkClass!, selector) as? UnityFrameworkProtocol {
                return instance
            }
        }
        return nil
    }
    
    @objc func getUnityView() -> UIView? {
        return unityFramework?.appController()?.rootView
    }
    
    @objc func sendMessage(_ gameObject: String, method: String, message: String) {
        unityFramework?.sendMessageToGO(
            withName: gameObject,
            functionName: method,
            message: message
        )
    }
    
    @objc func cleanup() {
        unityFramework?.unloadApplication()
        unityFramework = nil
    }
}
