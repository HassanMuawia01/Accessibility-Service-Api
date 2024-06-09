package com.example.accessibiltyservice

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.annotation.RequiresApi

class MyAccessibilityService : AccessibilityService() {

companion object {
    private var TAG = "AccessibilityService"
}
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.source == null || applicationContext == null) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {

            val rootNode = rootInActiveWindow ?: return
            val sourceNode = event.source
            Log.d(TAG,"sourceNode : ${event.source}")
            val clickedView = findClickedView(rootNode, sourceNode)
            Log.d(TAG,"sourceNode : ${clickedView}")
            if (clickedView != null) {
                val text = clickedView.text?.toString()
                Log.d(TAG,"sourceNode : ${text}")
                if (!text.isNullOrEmpty() && !clickedView.isClickable) {
                    showOverlayViewWithChanges(text, clickedView)
                }
            }
        }
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.DEFAULT
        serviceInfo = info
    }

    private fun findClickedView(root: AccessibilityNodeInfo, targetNode: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (root == targetNode) return root
        for (i in 0 until root.childCount) {
            val child = root.getChild(i)
            val found = findClickedView(child, targetNode)
            if (found != null) return found
        }
        return null
    }

    private fun showOverlayViewWithChanges(text: String, clickedView: AccessibilityNodeInfo) {
        val overlay = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        val textView = overlay.findViewById<TextView>(R.id.textView)
        textView.text = text

        // Calculate position for overlay
//        val rect = Rect()
//        clickedView.getBoundsInScreen(rect)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
//        params.x = rect.left
//        params.y = rect.top

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(overlay, params)

        overlay.setBackgroundColor(Color.RED)

        Handler().postDelayed({
            windowManager.removeView(overlay)
        }, 5000)
    }
}
