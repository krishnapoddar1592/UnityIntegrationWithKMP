# Unity Integration Guide for Kotlin Multiplatform Projects

## Overview
This guide explains how to integrate Unity into a Kotlin Multiplatform (KMP) project, focusing on Android implementation. You'll learn how to:
- Set up communication between Unity and KMP
- Add Unity animations to your project
- Control Unity animations from your KMP code

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

### Unity Activity
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

        // Register for broadcasts
        registerReceiver(
            receiver, 
            IntentFilter().apply {
                addAction("com.example.yourapp.START_ANIMATION")
                addAction("com.example.yourapp.CLEANUP")
            }, 
            RECEIVER_NOT_EXPORTED
        )

        setupLayout()
    }

    private fun setupLayout() {
        // Layout code as shown in previous responses...
    }

    private fun triggerParticleAnimation() {
        try {
            // This is how you send a message to Unity
            UnityPlayer.UnitySendMessage(
                "ParticleSystem",    // Must match GameObject name in Unity
                "StartAnimation",     // Must match method name in ParticleController
                ""                   // Parameters (if any)
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

### AndroidManifest.xml
```xml
<activity
    android:name=".AndroidUnityActivity"
    android:theme="@style/UnityThemeSelector"
    android:configChanges="mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|fontScale|layoutDirection|density"
    android:hardwareAccelerated="false"
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
   // In AndroidUnityActivity
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

### Important Points About Unity Messages
- GameObject name must exactly match what you use in UnitySendMessage
- Method name must exactly match between Unity and Android
- The method in Unity must be public
- Messages are one-way (Android to Unity)
- Unity callback methods must match the exact signature expected

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

