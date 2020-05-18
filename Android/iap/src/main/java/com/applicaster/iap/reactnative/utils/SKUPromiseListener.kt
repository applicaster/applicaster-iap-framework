package com.applicaster.iap.reactnative.utils

import android.util.Log
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.applicaster.iap.reactnative.IAPBridge
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap

class SKUPromiseListener(private val promise: Promise) : BillingListener {

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
    }

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

    override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
    }

    override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
    }

    override fun onBillingClientError(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

}