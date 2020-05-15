package com.applicaster.iap.reactnative

import com.facebook.react.bridge.*

class IAPBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val bridgeName = "IAPBridge"

    override fun getName(): String {
        return bridgeName
    }

}