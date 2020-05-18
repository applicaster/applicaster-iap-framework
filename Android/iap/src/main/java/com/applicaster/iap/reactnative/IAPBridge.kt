package com.applicaster.iap.reactnative

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.applicaster.iap.GoogleBillingHelper
import com.facebook.react.bridge.*

class IAPBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), BillingListener {
    private val bridgeName = "ApplicasterIAPBridge"

    init {
        GoogleBillingHelper.init(reactContext, this)
    }

    private val skuDetailsMap: MutableMap<String, SkuDetails> = mutableMapOf()

    override fun getName(): String {
        return bridgeName
    }

    @ReactMethod
    fun products(identifiers: ReadableArray, result: Promise) {
        val productIds = identifiers.toArrayList().map {it.toString()}
        GoogleBillingHelper.loadSkuDetails(BillingClient.SkuType.SUBS, productIds, asSKUListener(result))
    }

    private fun wrap(skuDetails: List<SkuDetails>): WritableNativeMap? {
        val products = WritableNativeArray()
        skuDetails.forEach {
            val skuDetail = WritableNativeMap()
            skuDetail.putString("price", it.price)
            skuDetail.putString("title", it.title)
            skuDetail.putString("description", it.description)
            products.pushMap(skuDetail)
        }
        val resolved = WritableNativeMap()
        resolved.putArray("products", products)
        return resolved
    }

    /**
     * Purchase item
     * @param {String} productIdentifier Dictionary with user data
     */
    @ReactMethod
    fun purchase(identifiers: String?, result: Promise) {
        val sku = skuDetailsMap.get(identifiers)
        if(null == sku){
            result.reject(IllegalArgumentException("SKU ${identifiers} not found"))
        }else {
            GoogleBillingHelper.purchase(reactApplicationContext.currentActivity!!, sku)
        }
    }
    /**
     * Restore Purchases
     */
    @ReactMethod
    fun restore(result: Promise) {
        result.resolve(true)
    }

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
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

    private fun asPurchaseListener(result: Promise): BillingListener {
        return object : BillingListener {
            override fun onPurchaseLoaded(purchases: List<Purchase>) {
            }

            override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
            }

            override fun onPurchasesRestored(purchases: List<Purchase>) {
            }

            override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
                result.resolve(wrap(skuDetails))
            }

            override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
                result.reject(statusCode.toString(), description)
            }

            override fun onPurchaseConsumed(purchaseToken: String) {
            }

            override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
            }

            override fun onBillingClientError(statusCode: Int, description: String) {
            }

        }
    }

    private fun asSKUListener(result: Promise): BillingListener {
        return object : BillingListener {
            override fun onPurchaseLoaded(purchases: List<Purchase>) {
                result.resolve(null)
            }

            override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
            }

            override fun onPurchasesRestored(purchases: List<Purchase>) {
            }

            override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
            }

            override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
                result.reject(statusCode.toString(), description)
            }

            override fun onPurchaseConsumed(purchaseToken: String) {
            }

            override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
            }

            override fun onBillingClientError(statusCode: Int, description: String) {
            }

        }
    }
}