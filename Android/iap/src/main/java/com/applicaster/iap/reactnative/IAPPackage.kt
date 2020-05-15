package com.applicaster.quickbrickinplayer.reactnative

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager

class InPlayerPackage: ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
        return mutableListOf(
                InPlayerAccountBridge(reactContext),
                InPlayerAssetBridge(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): MutableList<ViewManager<View, ReactShadowNode<*>>> {
       return mutableListOf()
    }
}