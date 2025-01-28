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

## 4. IOS implementation:
Will be updating it soon

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