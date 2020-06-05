package com.applicaster.iap.reactnative

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.applicaster.iap.GoogleBillingHelper
import com.applicaster.iap.reactnative.utils.*
import com.facebook.react.bridge.*

class IAPBridge(reactContext: ReactApplicationContext)
    : ReactContextBaseJavaModule(reactContext), BillingListener {

    companion object {
        const val bridgeName = "ApplicasterIAPBridge"
        const val TAG = bridgeName
    }

    init {
        GoogleBillingHelper.init(reactContext, this)
        GoogleBillingHelper.restorePurchasesForAllTypes()
    }

    private val purchaseListeners: MutableMap<String, Promise> = mutableMapOf()
    private val skuDetailsMap: MutableMap<String, SkuDetails> = mutableMapOf()
    private val purchasesMap: MutableMap<String, Purchase> = mutableMapOf()

    override fun getName(): String {
        return bridgeName
    }

    @ReactMethod
    fun products(identifiers: ReadableArray, result: Promise) {
        val productIds = identifiers.toArrayList().map {
            unwrapProductIdentifier(it as HashMap<String, String>)
        }.toMap()
        GoogleBillingHelper.loadSkuDetailsForAllTypes(productIds, SKUPromiseListener(result))
    }

    /**
     * Purchase item
     * @param {String} productIdentifier Dictionary with user data
     */
    @ReactMethod
    fun purchase(payload: ReadableMap, result: Promise) {
        val identifier = payload.getString("productIdentifier")
        // todo: finishTransactionAfterPurchase is not used now
        val finishTransactionAfterPurchase = payload.getBoolean("finishing")
        val sku = skuDetailsMap[identifier]
        if (null == sku) {
            result.reject(IllegalArgumentException("SKU $identifier not found"))
        } else {
            if(purchaseListeners.containsKey(sku.sku)) {
                result.reject(IllegalArgumentException("Another purchase is in progress for SKU $identifier"))
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

    /**
     *  Acknowledge
     */
    @ReactMethod
    fun finishPurchasedTransaction(transactionIdentifier: String, result: Promise) {
        GoogleBillingHelper.consume(transactionIdentifier, ConsumePromiseListener(result))
    }

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        purchases.forEach {
            purchasesMap[it.sku] = it
            purchaseListeners.remove(it.sku)?.resolve(wrap(it))
        }
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onPurchaseLoadingFailed: $statusCode $description")
        if(BillingClient.BillingResponse.ITEM_ALREADY_OWNED == statusCode) {
            purchaseListeners.forEach{
                val purchase = purchasesMap.get(it.key)
                if(null != purchase) {
                    it.value.resolve(wrap(purchase))
                } else {
                    it.value.reject(statusCode.toString(), description)
                }
            }
        } else {
            purchaseListeners.values.forEach{ it.reject(statusCode.toString(), description)}
        }
        purchaseListeners.clear()
    }

    override fun onPurchasesRestored(purchases: List<Purchase>) {
        purchases.forEach {
            purchasesMap[it.sku] = it
        }
    }

    override fun onSkuDetailsLoaded(skuDetails: List<SkuDetails>) {
        skuDetails.forEach {
            skuDetailsMap[it.sku] = it
        }
    }

    override fun onSkuDetailsLoadingFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onSkuDetailsLoadingFailed: $statusCode $description")
    }

    override fun onPurchaseConsumed(purchaseToken: String) {
    }

    override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onPurchaseConsumptionFailed: $statusCode $description")
    }

    override fun onBillingClientError(statusCode: Int, description: String) {
        Log.e(TAG, "onBillingClientError: $statusCode $description")
        purchaseListeners.values.forEach{ it.reject(statusCode.toString(), description)}
        purchaseListeners.clear()
    }

}