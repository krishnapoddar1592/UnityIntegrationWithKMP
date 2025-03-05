# Unity Integration Guide for Kotlin Multiplatform Projects

## Overview
This guide explains how to integrate Unity into a Kotlin Multiplatform (KMP) project, focusing on Android implementation. You'll learn how to:
- Set up communication between Unity and KMP
- Add Unity animations to your project
- Control Unity animations from your KMP code
- Implement using either XML or Jetpack Compose

## 1. Project Structure Setup

### KMP Project Structure
```plaintext
YourProject/
├── shared/
│   ├── src/
│   │   ├── commonMain/          # Common interfaces
│   │   └── androidMain/         # Android-specific implementations
└── androidApp/
    └── libs/
        └── unityLibrary.aar     # Your Unity export
```

### Required Files
1. Common Interface (in shared/commonMain):
```kotlin
interface UnityBridge {
   fun initialize()
   fun startParticleAnimation()
   fun cleanup()
}

expect class PlatformUnityBridge(context: Any?) : UnityBridge
```

2. Android Implementation (in shared/androidMain):
```kotlin
actual class PlatformUnityBridge actual constructor(private val context: Any?) : UnityBridge {
   override fun initialize() {
      (context as? Context)?.let { ctx ->
         try {
            // For XML layout use AndroidUnityActivity
            // For Compose use ComposeUnityActivity
            val unityActivityClass = Class.forName("com.example.yourapp.AndroidUnityActivity")
            val intent = Intent(ctx, unityActivityClass as Class<*>)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)
         } catch (e: Exception) {
            e.printStackTrace()
         }
      }
   }

   override fun startParticleAnimation() {
      (context as? Context)?.let { ctx ->
         val intent = Intent("com.example.yourapp.START_ANIMATION")
         ctx.sendBroadcast(intent)
      }
   }

   override fun cleanup() {
      (context as? Context)?.let { ctx ->
         val intent = Intent("com.example.yourapp.CLEANUP")
         ctx.sendBroadcast(intent)
      }
   }
}
```

## 2. Unity Setup

### Creating the Animation Controller
1. Create a new C# script named `ParticleController`:
```csharp
using UnityEngine;

public class ParticleController : MonoBehaviour
{
    private ParticleSystem particleSystem;

    void Start()
    {
        particleSystem = GetComponent<ParticleSystem>();
        if (particleSystem != null)
        {
            particleSystem.Stop(); // Start in stopped state
        }
    }

    // This method will be called from Android
    public void StartAnimation()
    {
        var particleSystem = GetComponent<ParticleSystem>();
        if (particleSystem != null)
        {
            particleSystem.Play();
        }
    }
}
```

### Unity Scene Setup
1. Create a GameObject named exactly "ParticleSystem"
2. Add a Particle System component
3. Attach the ParticleController script
4. Build as Android Library (AAR)

## 3. Android Implementation

### Option 1: XML-based Implementation

#### Unity Activity with XML Layout
```kotlin
class AndroidUnityActivity : UnityPlayerActivity() {
   private val receiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
         when (intent?.action) {
            "com.example.yourapp.START_ANIMATION" -> triggerParticleAnimation()
            "com.example.yourapp.CLEANUP" -> finish()
         }
      }
   }

   @RequiresApi(Build.VERSION_CODES.O)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // Register broadcast receiver
      registerReceiver(
         receiver,
         IntentFilter().apply {
            addAction("com.example.yourapp.START_ANIMATION")
            addAction("com.example.yourapp.CLEANUP")
         },
         RECEIVER_NOT_EXPORTED
      )

      // Create vertical LinearLayout as main container
      val mainLayout = LinearLayout(this).apply {
         orientation = LinearLayout.VERTICAL
         layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
         )
      }

      // Get Unity's frame layout
      val unityFrame = mUnityPlayer.getFrameLayout()
      (unityFrame.parent as? ViewGroup)?.removeView(unityFrame)

      // Set Unity frame to take 60% height
      unityFrame.layoutParams = LinearLayout.LayoutParams(
         ViewGroup.LayoutParams.MATCH_PARENT,
         0
      ).apply {
         weight = 0.6f
      }

      // Create button container for bottom 40%
      val buttonContainer = LinearLayout(this).apply {
         orientation = LinearLayout.VERTICAL
         gravity = Gravity.CENTER
         layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
         ).apply {
            weight = 0.4f
         }
         setBackgroundColor(android.graphics.Color.WHITE)
      }

      // Add button
      Button(this).apply {
         text = "Start Animation"
         layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
         ).apply {
            setMargins(32, 32, 32, 32)
         }
         setOnClickListener {
            triggerParticleAnimation()
         }
         buttonContainer.addView(this)
      }

      // Add views to containers
      mainLayout.addView(unityFrame)
      mainLayout.addView(buttonContainer)
      setContentView(mainLayout)
   }

   private fun triggerParticleAnimation() {
      try {
         UnityPlayer.UnitySendMessage(
            "ParticleSystem",
            "StartAnimation",
            ""
         )
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }

   override fun onDestroy() {
      unregisterReceiver(receiver)
      super.onDestroy()
   }
}
```

### Option 2: Compose Implementation

#### Unity Wrapper and Compose View
```kotlin
// UnityWrapper.kt
class UnityWrapper(
   private val activity: Activity,
   private val lifecycleEvents: IUnityPlayerLifecycleEvents
) {
   private var unityPlayer: UnityPlayerForActivityOrService? = null

   init {
      try {
         unityPlayer = UnityPlayerForActivityOrService(activity, lifecycleEvents).apply {
            frameLayout.requestFocus()
         }
         // Initialize Unity context
         UnityPlayer.currentActivity = activity
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }

   val view: FrameLayout? get() = unityPlayer?.frameLayout

   fun destroy() {
      unityPlayer?.destroy()
      unityPlayer = null
   }

   fun onPause() {
      unityPlayer?.onPause()
   }

   fun onResume() {
      unityPlayer?.onResume()
   }

   fun onConfigurationChanged(newConfig: Configuration) {
      unityPlayer?.configurationChanged(newConfig)
   }

   fun onWindowFocusChanged(hasFocus: Boolean) {
      unityPlayer?.windowFocusChanged(hasFocus)
   }

   fun sendMessage(gameObject: String, methodName: String, parameter: String = "") {
      try {
         UnityPlayer.UnitySendMessage(gameObject, methodName, parameter)
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }
}

@Composable
fun UnityView(
   modifier: Modifier = Modifier,
   onUnityPlayerCreated: (UnityWrapper) -> Unit = {}
) {
   val context = LocalContext.current
   val lifecycleOwner = LocalLifecycleOwner.current

   val unityWrapper = remember(context) {
      UnityWrapper(context as Activity, object : IUnityPlayerLifecycleEvents {
         override fun onUnityPlayerUnloaded() {}
         override fun onUnityPlayerQuitted() {}
      })
   }

   DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
         when (event) {
            Lifecycle.Event.ON_PAUSE -> unityWrapper.onPause()
            Lifecycle.Event.ON_RESUME -> unityWrapper.onResume()
            else -> {}
         }
      }

      lifecycleOwner.lifecycle.addObserver(observer)
      onUnityPlayerCreated(unityWrapper)

      onDispose {
         lifecycleOwner.lifecycle.removeObserver(observer)
         unityWrapper.destroy()
      }
   }

   AndroidView(
      modifier = modifier,
      factory = { _ ->
         unityWrapper.view?.apply {
            layoutParams = ViewGroup.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.MATCH_PARENT
            )
         } ?: FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.MATCH_PARENT
            )
         }
      }
   )
}

// ComposeUnityActivity.kt
class ComposeUnityActivity : ComponentActivity() {
   private var unityWrapper: UnityWrapper? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // Keep screen on
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

      setContent {
         Column(
            modifier = Modifier.fillMaxSize()
         ) {
            UnityView(
               modifier = Modifier
                  .weight(0.6f)
                  .fillMaxWidth(),
               onUnityPlayerCreated = { wrapper ->
                  unityWrapper = wrapper
               }
            )

            Column(
               modifier = Modifier
                  .weight(0.4f)
                  .fillMaxWidth()
                  .padding(16.dp)
            ) {
               Button(
                  onClick = {
                     unityWrapper?.sendMessage(
                        "ParticleSystem",
                        "StartAnimation",
                        ""
                     )
                  },
                  modifier = Modifier.fillMaxWidth()
               ) {
                  Text("Start Animation")
               }
            }
         }
      }
   }

   override fun onConfigurationChanged(newConfig: Configuration) {
      super.onConfigurationChanged(newConfig)
      unityWrapper?.onConfigurationChanged(newConfig)
   }

   override fun onWindowFocusChanged(hasFocus: Boolean) {
      super.onWindowFocusChanged(hasFocus)
      unityWrapper?.onWindowFocusChanged(hasFocus)
   }

   override fun onDestroy() {
      super.onDestroy()
      unityWrapper = null
   }
}
```

### AndroidManifest.xml Configuration
For both implementations:
```xml
<activity
        android:name=".AndroidUnityActivity"  <!-- or .ComposeUnityActivity -->
        android:configChanges="mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|fontScale|density"
        android:hardwareAccelerated="true"
        android:launchMode="singleTask"
        android:screenOrientation="fullUser">
<meta-data
android:name="unityplayer.UnityActivity"
android:value="true"
/>
        </activity>
```

## 4. Ios Implementation


### 4.1. Project Structure Setup

#### KMP Project Structure for iOS
```
YourProject/
├── shared/
│   ├── src/
│   │   ├── commonMain/          # Common interface definitions
│   │   └── iosMain/             # iOS-specific implementations
└── iosApp/
    ├── Frameworks/
    │   └── UnityFramework.framework  # Unity framework
    ├── Data/                    # Unity data folder with resources
    ├── iosApp-Bridging-Header.h  # Objective-C to Swift bridging header
    ├── UnityBridge.h            # Objective-C header for Unity communication
    ├── UnityBridge.m            # Objective-C implementation for Unity framework access
    └── UnityViewController.swift # Swift controller for Unity view
```

### 4.2. Required Files

###$ 4.2.1. Objective-C Bridge

**UnityBridge.h**:
```objc
#import <Foundation/Foundation.h>
#import <UnityFramework/UnityFramework.h>

@interface UnityBridge : NSObject
+ (UnityFramework *)getUnityFramework;
@end
```

**UnityBridge.m**:
```objc
#import "UnityBridge.h"
#import <mach-o/ldsyms.h>

// Handle different Mach-O types
#if EXECUTABLE
extern const struct mach_header_64 _mh_execute_header;
#define MACH_HEADER _mh_execute_header
#elif BUNDLE
extern const struct mach_header_64 _mh_bundle_header;
#define MACH_HEADER _mh_bundle_header
#else
extern const struct mach_header_64 _mh_dylib_header;
#define MACH_HEADER _mh_dylib_header
#endif

@implementation UnityBridge

+ (UnityFramework *)getUnityFramework
{
    NSString* bundlePath = [[NSBundle mainBundle] bundlePath];
    NSString* frameworkPath = [bundlePath stringByAppendingPathComponent: @"Frameworks/UnityFramework.framework"];
    
    NSBundle* bundle = [NSBundle bundleWithPath: frameworkPath];
    if ([bundle isLoaded] == false) [bundle load];
    
    UnityFramework* ufw = [bundle.principalClass getInstance];
    if (![ufw appController])
    {
        [ufw setExecuteHeader:(void*)&MACH_HEADER];
    }
    [ufw setDataBundleId:@"com.unity3d.framework"];
    return ufw;
}

@end
```

#### 4.2.2. Swift Unity View Controller

**UnityViewController.swift**:
```swift
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
        
        // Initialize Unity on the main thread
        DispatchQueue.main.async {
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
        
        // Add Unity view as subview
        if let unityView = framework.appController()?.rootView {
            view.addSubview(unityView)
            unityView.frame = view.bounds
            
            if let loadingIndicator = view.viewWithTag(100) {
                loadingIndicator.removeFromSuperview()
            }
            
            // Wait before sending message
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
```

#### 4.2.3. SwiftUI Integration

**ContentView.swift**:
```swift
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

// Optional: UIViewControllerRepresentable wrapper for embedding Unity in SwiftUI
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
```

#### 4.2.4. UIKit-based Alternative (if needed)

**ViewController.swift**:
```swift
import UIKit
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
```

#### 4.2.5. Bridging Header

**iosApp-Bridging-Header.h**:
```objc
#import "UnityBridge.h"
```

### 4.3. KMP Shared Code Integration

#### 4.3.1. Common Interface in commonMain

```kotlin
interface UnityBridge {
    fun initialize()
    fun startParticleAnimation()
    fun stopParticleAnimation()
    fun cleanup()
}
```


### 4.4. Unity Export and Project Setup

#### 4.4.1. Unity Export for iOS

1. In Unity, go to **File > Build Settings**
2. Select **iOS** as the platform
3. Check **Development Build** for debugging capabilities
4. Ensure **Script Debugging** is disabled for release builds
5. Click **Build** and choose a folder to save the export
   6. If using Mac M-series chips select arm64 architecture


#### 4.4.2. Integration Steps

1. From the Unity export folder, copy:
   - `UnityFramework.framework` from the build products folder
   - The entire `Data` folder

2. Add these to your Xcode project:
   - Add `UnityFramework.framework` to "Frameworks, Libraries, and Embedded Content", find this file from the exported xcode project and find its location in finder(Note: its not the UnityFramework.h file, its UnityFramework.Framework)
   - Add the `Data` folder as a folder reference (blue folder, not a group)
   - Make sure "Copy items if needed" is checked

#### 4.4.3. Critical Xcode Build Settings

Based on your build settings, ensure these configurations:

```
FRAMEWORK_SEARCH_PATHS = $(inherited) $(SRCROOT)/../shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME) $(SRCROOT)/Frameworks $(PROJECT_DIR) $(PROJECT_DIR)/Frameworks $(PROJECT_DIR)/iosApp/Frameworks

OTHER_LDFLAGS = $(inherited) -framework UnityFramework

SWIFT_OBJC_BRIDGING_HEADER = iosApp-Bridging-Header.h
```

Additionally, ensure these settings:

1. **Simulator Configuration**:
   - Edit your scheme (Product > Scheme > Edit Scheme)
   - Under "Run", uncheck "Debug executable"
   - This is critical for preventing crashes in the simulator

2. **Bitcode**:
   - Set "Enable Bitcode" to NO

### 4.5. Common Issues and Solutions

#### 4.5.1 Unity Data Path Issue

If you get "IL2CPP initialization failed" or "Could not find global-metadata.dat":
- Ensure `[ufw setDataBundleId:@"com.unity3d.framework"]` is set in UnityBridge.m
- Verify the Data folder is correctly included in your app bundle

#### 4.5.2 Thread Safety Issues

If you encounter "BUG IN CLIENT OF LIBDISPATCH: trying to lock recursively":
- Make sure "Debug executable" is unchecked in the scheme settings
- Ensure all Unity initialization happens on the main thread
- Avoid recursive calls to getUnityFramework

#### 4.5.3 Unity View Not Appearing

If the Unity view doesn't appear:
- Check UnityFramework is properly loaded
- Verify Data folder structure and inclusion
- Check for console errors during initialization
- Ensure the device/simulator supports Metal rendering

### 4.6. Testing

1. **Check in Simulator First**:
   - Run on iOS Simulator
   - Verify Unity loads and animations play
   - Check for any errors in the console

2. **Test on Real Device**:
   - Unity performance is better on physical devices
   - Verify all animations render correctly

### 4.7. Build Settings Reference

Key build settings from your successful configuration:
```
FRAMEWORK_SEARCH_PATHS = $(inherited) $(SRCROOT)/../shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME) $(SRCROOT)/Frameworks $(PROJECT_DIR) $(PROJECT_DIR)/Frameworks
OTHER_LDFLAGS = $(inherited) -framework UnityFramework
SWIFT_OBJC_BRIDGING_HEADER = iosApp-Bridging-Header.h
ENABLE_USER_SCRIPT_SANDBOXING = NO
```



## 5. Communication Flow

### How Messages Flow
1. **KMP to Android**:
   ```kotlin
   // In your KMP code
   val bridge = PlatformUnityBridge(context)
   bridge.startParticleAnimation() // This sends a broadcast
   ```

2. **Android to Unity**:
   ```kotlin
   // In both XML and Compose implementations
   UnityPlayer.UnitySendMessage(
       "ParticleSystem",    // GameObject name
       "StartAnimation",    // Method name
       ""                  // Parameters
   )
   ```

3. **Unity Receives**:
   ```csharp
   // In ParticleController.cs
   public void StartAnimation() {
       // Method is called by Android
   }
   ```

## 6. Common Issues and Solutions

1. **Message Not Received in Unity**
   - Check GameObject name matches exactly
   - Verify method is public in Unity script
   - Ensure ParticleController is attached to correct GameObject

2. **NoSuchMethodException**
   - Use UnityPlayer.UnitySendMessage (static method)
   - Don't use reflection for UnitySendMessage

3. **Unity View Issues**
   - Ensure proper lifecycle handling
   - Check manifest configuration
   - Verify Unity AAR is properly included

4. **Compose-specific Issues**
   - Make sure UnityWrapper is properly initialized
   - Handle configuration changes correctly
   - Maintain proper lifecycle management