package com.applicaster.iap.reactnative.utils


import com.applicaster.iap.uni.api.IAPListener
import com.applicaster.iap.uni.api.IBillingAPI
import com.applicaster.iap.uni.api.Purchase
import com.applicaster.iap.uni.api.Sku
import com.facebook.react.bridge.Promise

abstract class PromiseListener(protected val promise: Promise) : IAPListener {

    override fun onPurchased(purchase: Purchase) {
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
    }

    override fun onSkuDetailsLoaded(skuDetails: List<Sku>) {
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
    }

    override fun onPurchaseAcknowledged() {
    }

    override fun onSkuDetailsLoadingFailed(result: IBillingAPI.IAPResult, description: String) {
        promise.reject(result.toString(), description)
    }

    override fun onPurchaseRestoreFailed(result: IBillingAPI.IAPResult, description: String) {
        promise.reject(result.toString(), description)
    }

    override fun onPurchaseFailed(result: IBillingAPI.IAPResult, description: String) {
        promise.reject(result.toString(), description)
    }

    override fun onPurchaseConsumptionFailed(result: IBillingAPI.IAPResult, description: String) {
        promise.reject(result.toString(), description)
    }

    override fun onBillingClientError(result: IBillingAPI.IAPResult, description: String) {
        promise.reject(result.toString(), description)
    }

    override fun onPurchaseAcknowledgeFailed(result: IBillingAPI.IAPResult, description: String) {
        promise.reject(result.toString(), description)
    }
}
