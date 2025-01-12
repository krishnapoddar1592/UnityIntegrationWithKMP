import Foundation
import UIKit
import UnityFramework

@objc public class UnityFrameworkWrapper: NSObject{
    @objc public static let shared = UnityFrameworkWrapper()
    private var unityFramework: UnityFramework?
    
    private override init() {
        super.init()
    }
    
    // Add the UnityFrameworkListener protocol methods
    public func unityDidUnload(_ notification: Notification!) {
        // Handle Unity unload event
    }
    
    public func unityDidQuit(_ notification: Notification!) {
        // Handle Unity quit event
    }
    
    @objc public func initialize() {
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
    
    private func getUnityFramework() -> UnityFramework? {
        let bundlePath = Bundle.main.bundlePath
        let frameworkPath = bundlePath + "/Frameworks/UnityFramework.framework"
        let bundle = Bundle(path: frameworkPath)
        if bundle?.isLoaded == false {
            bundle?.load()
        }
        
        let frameworkClassName = "UnityFramework"
        guard let frameworkClass = NSClassFromString(frameworkClassName) as? UnityFramework.Type else {
            return nil
        }
        
        return frameworkClass.getInstance()
    }
    
    @objc public func getUnityView() -> UIView? {
        return unityFramework?.appController()?.rootView
    }
    
    @objc public func sendMessage(_ gameObject: String, method: String, message: String) {
        unityFramework?.sendMessageToGO(
            withName: gameObject,
            functionName: method,
            message: message
        )
    }
    
    @objc public func cleanup() {
        unityFramework?.unloadApplication()
        unityFramework = nil
    }
}
