package com.applicaster.iap.reactnative.utils

import com.applicaster.iap.reactnative.IAPBridge
import com.applicaster.iap.uni.api.IBillingAPI
import com.applicaster.iap.uni.api.Purchase
import com.facebook.react.bridge.Promise

class FinishingPurchasePromiseListener(private val bridge: IAPBridge,
                                       promise: Promise)
    : PurchasePromiseListener(promise) {

    private lateinit var purchase: Purchase

    override fun onPurchased(purchase: Purchase) {
        this.purchase = purchase
        bridge.acknowledge(
                purchase.productIdentifier,
                purchase.transactionIdentifier,
                this)
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
        promise.resolve(wrap(purchase))
    }

    override fun onPurchaseAcknowledged() {
        promise.resolve(wrap(purchase))
    }
}
