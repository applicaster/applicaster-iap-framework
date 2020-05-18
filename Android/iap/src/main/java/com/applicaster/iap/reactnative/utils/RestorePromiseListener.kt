package com.applicaster.iap.reactnative.utils

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.facebook.react.bridge.Promise

class RestorePromiseListener(private val promise: Promise) : BillingListener {

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        val purchasedItemIDs = purchases.map { rmapID(it.sku) }
        promise.resolve(purchasedItemIDs)
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
        val purchasedItemIDs = purchases.map { rmapID(it.sku) }
        promise.resolve(purchasedItemIDs)
    }

    override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
    }

    override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
    }

    override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onBillingClientError(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

}