package com.applicaster.iap.reactnative.utils

import com.applicaster.iap.reactnative.IAPBridge
import com.applicaster.iap.uni.api.Purchase
import com.facebook.react.bridge.Promise

class FinishingPurchasePromiseListener(private val bridge: IAPBridge,
                                       private val skuOverride: String?,
                                       promise: Promise)
    : PurchasePromiseListener(promise, skuOverride) {

    private lateinit var purchase: Purchase

    override fun onPurchased(purchase: Purchase) {
        // hack for Amazon: actual purchase for subscriptions will have another SKU (Parent one)
        this.purchase = when (skuOverride) {
            null -> purchase
            else -> Purchase(skuOverride, purchase.transactionIdentifier, purchase.receipt)
        }
        bridge.acknowledge(
                skuOverride?:purchase.productIdentifier,
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
