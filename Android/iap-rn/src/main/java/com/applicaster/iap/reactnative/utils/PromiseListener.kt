package com.applicaster.iap.reactnative.utils

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.facebook.react.bridge.Promise

abstract class PromiseListener(protected val promise: Promise) : BillingListener {

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
    }

    override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
    }

    override fun onPurchaseAcknowledged() {
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onBillingClientError(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }

    override fun onPurchaseAcknowledgeFailed(statusCode: Int, description: String) {
        promise.reject(statusCode.toString(), description)
    }
}
