package com.applicaster.iap.reactnative.utils

import com.applicaster.iap.uni.api.Purchase
import com.facebook.react.bridge.Promise

open class PurchasePromiseListener(promise: Promise) : PromiseListener(promise) {

    override fun onPurchased(purchase: Purchase) {
        promise.resolve(wrap(purchase))
    }
}