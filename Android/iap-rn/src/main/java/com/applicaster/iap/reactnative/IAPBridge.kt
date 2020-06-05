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

    // map SKU been purchased to listened and a flag indicating whether instant acknowledge is needed
    private val purchaseListeners: MutableMap<String, Pair<Promise, Boolean>> = mutableMapOf()
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
        val finishTransactionAfterPurchase = payload.getBoolean("finishing")
        val sku = skuDetailsMap[identifier]
        if (null == sku) {
            result.reject(IllegalArgumentException("SKU $identifier not found"))
        } else {
            if(purchaseListeners.containsKey(sku.sku)) {
                result.reject(IllegalArgumentException("Another purchase is in progress for SKU $identifier"))
                return
            }
            this.purchaseListeners[sku.sku] = Pair(result, finishTransactionAfterPurchase)
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
    fun finishPurchasedTransaction(transaction: ReadableMap, result: Promise) {
        val identifier = transaction.getString("productIdentifier")!!
        val transactionIdentifier = transaction.getString("transactionIdentifier")!!
        acknowledge(identifier, transactionIdentifier, result)
    }

    private fun acknowledgeIfNeeded(it: Pair<Promise, Boolean>, p: Purchase) {
        if (!it.second) {
            it.first.resolve(wrap(p))
        } else {
            acknowledge(p.sku, p.purchaseToken, it.first)
        }
    }

    private fun acknowledge(identifier: String, transactionIdentifier: String, result: Promise) {
        val skuDetails = skuDetailsMap[identifier]
        if (null != skuDetails) {
            if (BillingClient.SkuType.INAPP == skuDetails.type) {
                GoogleBillingHelper.consume(transactionIdentifier, ConsumePromiseListener(result))
            }
        } else {
            result.reject(
                    "SKU details not loaded $transactionIdentifier",
                    IllegalArgumentException("SKU details not loaded $transactionIdentifier"))
        }
    }

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        purchases.forEach {p ->
            purchasesMap[p.sku] = p
            purchaseListeners.remove(p.sku)?.let {
                acknowledgeIfNeeded(it, p)
            }
        }
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onPurchaseLoadingFailed: $statusCode $description")
        if(BillingClient.BillingResponse.ITEM_ALREADY_OWNED == statusCode) {
            purchaseListeners.forEach{
                val purchase = purchasesMap.get(it.key)
                if(null != purchase) {
                    acknowledgeIfNeeded(it.value, purchase)
                } else {
                    it.value.first.reject(statusCode.toString(), description)
                }
            }
        } else {
            purchaseListeners.values.forEach{ it.first.reject(statusCode.toString(), description)}
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
        purchaseListeners.values.forEach{ it.first.reject(statusCode.toString(), description)}
        purchaseListeners.clear()
    }

}