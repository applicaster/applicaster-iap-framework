package com.applicaster.iap.reactnative.utils

import android.util.Log
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.reactnative.IAPBridge
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap

class SKUPromiseListener(promise: Promise) : PromiseListener(promise) {

    override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
        Log.d(IAPBridge.TAG, "${skuDetails.size} SKUs loaded");
        val products = WritableNativeArray()
        skuDetails.forEach {
            products.pushMap(wrap(it))
        }
        val resolved = WritableNativeMap()
        resolved.putArray("products", products)
        promise.resolve(resolved)
    }
}
