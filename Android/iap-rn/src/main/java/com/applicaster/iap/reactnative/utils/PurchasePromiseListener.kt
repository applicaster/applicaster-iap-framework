package com.applicaster.iap.reactnative.utils

import com.applicaster.iap.uni.api.Purchase
import com.facebook.react.bridge.Promise

open class PurchasePromiseListener(promise: Promise,
                                   private val skuOverride: String?) : PromiseListener(promise) {

    override fun onPurchased(purchase: Purchase) {
        // hack for Amazon: actual purchase for subscriptions will have another SKU (Parent one)
        val purchase = when (skuOverride) {
            null -> purchase
            else -> Purchase(skuOverride, purchase.transactionIdentifier, purchase.receipt)
        }
        promise.resolve(wrap(purchase))
    }
}