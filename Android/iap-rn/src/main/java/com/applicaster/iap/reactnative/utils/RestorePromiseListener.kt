package com.applicaster.iap.reactnative.utils

import com.android.billingclient.api.Purchase
import com.facebook.react.bridge.Promise

class RestorePromiseListener(promise: Promise) : PromiseListener(promise) {

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        val purchasesArray = WritableNativeArray()
        purchases.forEach { purchasesArray.pushMap(wrap(it)) }
        promise.resolve(purchasesArray)
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
        val purchasedItemIDsArray = WritableNativeArray()
        purchases.forEach { purchasedItemIDsArray.pushMap(wrap(it)) }
        promise.resolve(purchasedItemIDsArray)
    }

}