package com.example.simplecamera

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.simplecamera.theme.SimpleCameraTheme
import com.example.simplecamera.ui.camera.CameraScreen

class MainActivity : ComponentActivity() {

  private fun isCameraLaunch(intentToCheck: Intent?): Boolean {
    val action = intentToCheck?.action
    return action in listOf(
      "android.media.action.STILL_IMAGE_CAMERA",
      "android.media.action.STILL_IMAGE_CAMERA_SECURE",
      "android.media.action.VIVO_STILL_IMAGE_CAMERA_SECURE",
      "android.media.action.IMAGE_CAPTURE",
      "android.media.action.IMAGE_CAPTURE_SECURE",
      "android.media.action.VIVO_IMAGE_CAPTURE"
    )
  }

  private fun applyImmersiveHostageMode() {
    if (HostageManager.isHostageActive()) {
      window.addFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN or
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
      )

      val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
      windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

      @Suppress("DEPRECATION")
      window.decorView.systemUiVisibility = (
        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_FULLSCREEN
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      )

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.decorView.post {
          val height = window.decorView.height
          val width = window.decorView.width
          if (height > 0 && width > 0) {
            val exclusionRect = Rect(0, height - 350, width, height)
            window.decorView.systemGestureExclusionRects = listOf(exclusionRect)
          }
        }
      }
    } else {
      window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
      val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
      windowInsetsController.show(WindowInsetsCompat.Type.navigationBars())
      @Suppress("DEPRECATION")
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.decorView.post {
          window.decorView.systemGestureExclusionRects = emptyList()
        }
      }
    }
  }

  private fun reclaimForeground() {
    if (HostageManager.isHostageActive()) {
      try {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        am?.appTasks?.firstOrNull()?.moveToFront()
      } catch (e: Exception) {
        // Fallback
      }
      try {
        val reclaimIntent = Intent(this, MainActivity::class.java).apply {
          addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
            Intent.FLAG_ACTIVITY_SINGLE_TOP
          )
        }
        startActivity(reclaimIntent)
      } catch (e: Exception) {
        // Fallback
      }
    }
  }

  // Detects when the user unlocks their phone (PIN, fingerprint, face, pattern)
  private val userPresentReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_USER_PRESENT) {
        HostageManager.reset()
        applyImmersiveHostageMode()
        Toast.makeText(this@MainActivity, "🔓 Device unlocked: Hostage mode released", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Allow displaying over lockscreen and waking screen on double-tap power button
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
    } else {
      @Suppress("DEPRECATION")
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
      )
    }

    if (isCameraLaunch(intent)) {
      HostageManager.isHostageMode = true
      HostageManager.isPhotoCompleted = false
    } else {
      HostageManager.reset()
    }

    try {
      registerReceiver(userPresentReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
    } catch (e: Exception) {
      // Ignore
    }

    // Intercept Back gestures and system back in Android 13/14
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (HostageManager.isHostageActive()) {
          Toast.makeText(
            this@MainActivity,
            "🔒 HOSTAGE LOCK: You cannot exit to lockscreen until you satisfy PEAK alignment and shoot!",
            Toast.LENGTH_SHORT
          ).show()
        } else {
          isEnabled = false
          finish()
        }
      }
    })

    WindowCompat.setDecorFitsSystemWindows(window, false)
    applyImmersiveHostageMode()

    // Enforce immersive mode whenever insets change
    window.decorView.setOnApplyWindowInsetsListener { view, insets ->
      if (HostageManager.isHostageActive()) {
        applyImmersiveHostageMode()
      }
      view.onApplyWindowInsets(insets)
    }

    setContent {
      SimpleCameraTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          CameraScreen(isLockscreenHostage = HostageManager.isHostageMode)
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    applyImmersiveHostageMode()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      applyImmersiveHostageMode()
    } else if (HostageManager.isHostageActive()) {
      reclaimForeground()
    }
  }

  override fun onUserLeaveHint() {
    super.onUserLeaveHint()
    if (HostageManager.isHostageActive()) {
      Toast.makeText(
        this,
        "🔒 HOSTAGE LOCK: Exit gesture blocked! Complete PEAK Mode to escape!",
        Toast.LENGTH_SHORT
      ).show()
      reclaimForeground()
    }
  }

  override fun onPause() {
    super.onPause()
    if (HostageManager.isHostageActive()) {
      reclaimForeground()
    }
  }

  override fun onStop() {
    super.onStop()
    if (HostageManager.isHostageActive()) {
      reclaimForeground()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      unregisterReceiver(userPresentReceiver)
    } catch (e: Exception) {
      // Ignore
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    if (isCameraLaunch(intent)) {
      HostageManager.isHostageMode = true
      HostageManager.isPhotoCompleted = false
      applyImmersiveHostageMode()
    } else {
      HostageManager.reset()
      applyImmersiveHostageMode()
    }
  }

  // Intercept physical/navigation-bar Back button and volume buttons at hardware key level
  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (HostageManager.isHostageActive()) {
        Toast.makeText(
          this,
          "🔒 HOSTAGE LOCK: You cannot exit to lockscreen until you satisfy PEAK alignment and shoot!",
          Toast.LENGTH_SHORT
        ).show()
        return true // CONSUMED: Hardware/Navbar Back completely blocked!
      }
    }

    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
      val direction = if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
      audioManager?.adjustStreamVolume(AudioManager.STREAM_RING, direction, AudioManager.FLAG_SHOW_UI)
      return true // Consumed: Prevents camera shutter shortcut trigger!
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (HostageManager.isHostageActive()) {
        return true // CONSUMED!
      }
    }
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      return true // Consumed!
    }
    return super.onKeyUp(keyCode, event)
  }

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    if (HostageManager.isHostageActive()) {
      Toast.makeText(
        this,
        "🔒 HOSTAGE LOCK: You cannot exit to lockscreen until you satisfy PEAK alignment and shoot!",
        Toast.LENGTH_SHORT
      ).show()
      return
    }
    super.onBackPressed()
  }
}
