




import UIKit
import UnityFramework

class UnityViewController: UIViewController, UnityFrameworkListener {
    private var unityFramework: UnityFramework?
    private static var hasInitialized = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        
        // Add a loading indicator
        let loadingIndicator = UIActivityIndicatorView(style: .large)
        loadingIndicator.center = view.center
        loadingIndicator.color = .white
        loadingIndicator.startAnimating()
        view.addSubview(loadingIndicator)
        loadingIndicator.tag = 100
        
        // Initialize Unity on the MAIN thread, not a background thread
        DispatchQueue.main.async { // Changed from asyncAfter or global queue
            self.initUnity()
        }
    }
    
    private func initUnity() {
        // Skip if already initialized
        if UnityViewController.hasInitialized {
            // Just add the Unity view
            if let unityView = UnityBridge.getUnityFramework()?.appController()?.rootView {
                view.addSubview(unityView)
                unityView.frame = view.bounds
                
                if let loadingIndicator = view.viewWithTag(100) {
                    loadingIndicator.removeFromSuperview()
                }
            }
            return
        }
        
        // Get framework
        guard let framework = UnityBridge.getUnityFramework() else {
            print("Failed to get Unity framework")
            return
        }
        
        // Only set up and run if not already running
        if framework.appController() == nil {
            framework.register(self)
            
            // Run Unity
            framework.runEmbedded(
                withArgc: CommandLine.argc,
                argv: CommandLine.unsafeArgv,
                appLaunchOpts: nil
            )
        }
        
        unityFramework = framework
        UnityViewController.hasInitialized = true
        
        // Add Unity view as subview (on main thread)
        if let unityView = framework.appController()?.rootView {
            view.addSubview(unityView)
            unityView.frame = view.bounds
            
            if let loadingIndicator = view.viewWithTag(100) {
                loadingIndicator.removeFromSuperview()
            }
            
            // Wait before sending message (still on main thread)
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                self.controlParticles(command: "start")
            }
        }
    }
    
    // Required for UnityFrameworkListener
    func unityDidUnload(_ notification: Notification!) {
        print("Unity did unload")
        unityFramework = nil
        UnityViewController.hasInitialized = false
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        if let rootView = unityFramework?.appController()?.rootView {
            rootView.frame = view.bounds
        }
    }
    
    func controlParticles(command: String) {
        print("Sending command to Unity: \(command)")
        unityFramework?.sendMessageToGO(
            withName: "ParticleSystem",
            functionName: "HandleParticleCommand",
            message: command
        )
    }
}
