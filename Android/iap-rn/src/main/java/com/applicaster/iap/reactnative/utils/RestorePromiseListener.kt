package com.applicaster.iap.reactnative.utils

import com.android.billingclient.api.Purchase
import com.facebook.react.bridge.Promise

class RestorePromiseListener(promise: Promise) : PromiseListener(promise) {

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        val purchasedItemIDs = purchases.map { it.sku }
        promise.resolve(purchasedItemIDs)
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
        val purchasedItemIDs = purchases.map { it.sku }
        promise.resolve(purchasedItemIDs)
    }

}