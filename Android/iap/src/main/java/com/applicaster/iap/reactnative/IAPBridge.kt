package com.applicaster.iap.reactnative

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.applicaster.iap.GoogleBillingHelper
import com.applicaster.iap.reactnative.utils.RestorePromiseListener
import com.applicaster.iap.reactnative.utils.SKUPromiseListener
import com.applicaster.iap.reactnative.utils.mapID
import com.applicaster.iap.reactnative.utils.wrap
import com.facebook.react.bridge.*

class IAPBridge(reactContext: ReactApplicationContext)
    : ReactContextBaseJavaModule(reactContext), BillingListener {

    companion object {
        const val bridgeName = "ApplicasterIAPBridge"
        const val TAG = bridgeName
    }

    init {
        GoogleBillingHelper.init(reactContext, this)
    }

    private val purchaseListeners: MutableMap<String, Promise> = mutableMapOf()
    private val skuDetailsMap: MutableMap<String, SkuDetails> = mutableMapOf()

    override fun getName(): String {
        return bridgeName
    }

    @ReactMethod
    fun products(identifiers: ReadableArray, result: Promise) {
        val productIds = identifiers.toArrayList().map {mapID(it.toString())}.toMutableList();
        GoogleBillingHelper.loadSkuDetails(BillingClient.SkuType.SUBS, productIds, SKUPromiseListener(result))
    }

    /**
     * Purchase item
     * @param {String} productIdentifier Dictionary with user data
     */
    @ReactMethod
    fun purchase(identifier: String?, result: Promise) {
        val sku = skuDetailsMap[mapID(identifier!!)]
        if (null == sku) {
            result.reject(IllegalArgumentException("SKU ${identifier} not found"))
        } else {
            if(purchaseListeners.containsKey(sku.sku)) {
                result.reject(IllegalArgumentException("Another purchase is in progress for SKU ${identifier}"))
                return
            }
            this.purchaseListeners[sku.sku] = result
            GoogleBillingHelper.purchase(reactApplicationContext.currentActivity!!, sku)
        }
    }

    /**
     * Restore Purchases
     */
    @ReactMethod
    fun restore(result: Promise) {
        GoogleBillingHelper.restorePurchasesForAllTypes(RestorePromiseListener(result))
    }

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        purchases.forEach {
            purchaseListeners.remove(it.sku)?.resolve(wrap(it))
        }
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
    }

    override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
        skuDetails.forEach {
            skuDetailsMap[it.sku] = it
        }
    }

    override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
    }

    override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
    }

    override fun onBillingClientError(statusCode: Int, description: String) {
    }

}