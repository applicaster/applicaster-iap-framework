package com.applicaster.iap.reactnative.utils

import com.applicaster.iap.reactnative.IAPBridge
import com.applicaster.iap.uni.api.Purchase
import com.facebook.react.bridge.Promise

class FinishingPurchasePromiseListener(private val bridge: IAPBridge, promise: Promise)
    : PurchasePromiseListener(promise) {

    override fun onPurchased(purchase: Purchase) {
        bridge.acknowledge(
                purchase.productIdentifier,
                purchase.transactionIdentifier,
                promise)
    }

}