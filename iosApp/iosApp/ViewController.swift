//
//  ViewController.swift
//  iosApp
//
//  Created by Krishna Poddar on 04/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//


//  ViewController.swift
//  TestUnity
//
//  Created by Krishna Poddar on 24/02/25.
//
import os.log
// Create a dedicated logger
private let logger = OSLog(subsystem: "com.chatsdk.unitydemo", category: "ThreadDebug")

class ViewController: UIViewController {
    override func viewDidLoad() {
        print("DEBUG: About to initialize Unity")
        NSLog("UNITY_INTEGRATION: About to initialize Unity")
        super.viewDidLoad()
        os_log("ViewController.viewDidLoad called on thread: %{public}@", log: logger, type: .debug, Thread.current.description)
        
        // Add a button to present Unity view
        let button = UIButton(frame: CGRect(x: 100, y: 100, width: 200, height: 50))
        button.setTitle("Show Unity View", for: .normal)
        button.setTitleColor(.blue, for: .normal)
        button.addTarget(self, action: #selector(showUnity), for: .touchUpInside)
        view.addSubview(button)
    }
    
    @objc func showUnity() {
        os_log("showUnity called on thread: %{public}@", log: logger, type: .debug, Thread.current.description)
        let unityVC = UnityViewController()
        present(unityVC, animated: true)
    }
}
