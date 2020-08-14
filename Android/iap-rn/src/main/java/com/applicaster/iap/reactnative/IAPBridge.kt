package com.applicaster.iap.reactnative

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.applicaster.iap.BillingListener
import com.applicaster.iap.GoogleBillingHelper
import com.applicaster.iap.reactnative.utils.*
import com.facebook.react.bridge.*


// todo: restore on already owned
// todo: reveret purchase if not fulfilled

class IAPBridge(reactContext: ReactApplicationContext)
    : ReactContextBaseJavaModule(reactContext), BillingListener {

    companion object {
        const val bridgeName = "ApplicasterIAPBridge"
        const val TAG = bridgeName

        const val subscription = "subscription"
        const val nonConsumable = "nonConsumable"
        const val consumable = "consumable"

        // map product fields for IAP library and update sky type lookup table
        private fun unwrapProductIdentifier(map: HashMap<String, String>,
                                            skuTypesCache: MutableMap<String, String>): Pair<String, String> {
            val productId = map["productIdentifier"]!!
            val productType = map["productType"]!!
            skuTypesCache[productId] = productType
            return Pair(
                    productId,
                    when (productType) {
                        subscription -> BillingClient.SkuType.SUBS
                        nonConsumable -> BillingClient.SkuType.INAPP
                        consumable -> BillingClient.SkuType.INAPP
                        else -> throw IllegalArgumentException("Unknown SKU type ${map["productType"]}")
                    })
        }
    }

    init {
        GoogleBillingHelper.init(reactContext, this)
        GoogleBillingHelper.restorePurchasesForAllTypes()
    }

    // map SKU been purchased to listened and a flag indicating whether instant acknowledge is needed
    private val purchaseListeners: MutableMap<String, Pair<Promise, Boolean>> = mutableMapOf()
    private var currentPurchaseListener: Promise? = null

    // lookup map to SkuDetails
    private val skuDetailsMap: MutableMap<String, SkuDetails> = mutableMapOf()

    // map of purchases owned
    private val purchasesMap: MutableMap<String, Purchase> = mutableMapOf()
    private var currentPurchaseForAcknowledge: Purchase? = null

    // cache for initial purchase type since Google billing does not distinguish consumables and non-consumables
    private val skuTypes: MutableMap<String, String> = mutableMapOf()

    override fun getName(): String {
        return bridgeName
    }

    @ReactMethod
    fun products(identifiers: ReadableArray, result: Promise) {
        val productIds = identifiers.toArrayList().map {
            unwrapProductIdentifier(it as HashMap<String, String>, skuTypes)
        }.toMap()
        GoogleBillingHelper.loadSkuDetailsForAllTypes(productIds, SKUPromiseListener(result))
    }

    /**
     * Purchase item
     * @param payload Dictionary with purchase data and flow information
     */
    @ReactMethod
    fun purchase(payload: ReadableMap, result: Promise) {
        val identifier = payload.getString("productIdentifier")
        val finishTransactionAfterPurchase = payload.getBoolean("finishing")
        val sku = skuDetailsMap[identifier]
        if (null == sku) {
            result.reject(IllegalArgumentException("SKU $identifier not found"))
        } else {
            // in fact, only one purchase process can be running at a time, so its not needed
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

    private fun acknowledge(identifier: String, transactionIdentifier: String, result: Promise) {
        val skuDetails = skuDetailsMap[identifier]
        if (null == skuDetails) {
            result.reject(IllegalArgumentException("SKU details are not loaded $transactionIdentifier"))
            return
        }
        if (BillingClient.SkuType.INAPP == skuDetails.type) {
            when (skuTypes[identifier]) {
                subscription -> result.reject(RuntimeException("InApp acknowledge flow triggered for subscription"))
                nonConsumable -> GoogleBillingHelper.acknowledge(transactionIdentifier, null)
                consumable -> GoogleBillingHelper.consume(transactionIdentifier, ConsumePromiseListener(result))
                null -> result.reject(IllegalArgumentException("SKU type details for $identifier is not loaded $transactionIdentifier"))
                else -> result.reject(IllegalArgumentException("SKU type ${skuTypes[identifier]} not handled in acknowledge $transactionIdentifier"))
            }
        } else if (BillingClient.SkuType.SUBS == skuDetails.type) {
            GoogleBillingHelper.acknowledge(transactionIdentifier, null)
        }
    }

    private fun acknowledgeIfNeeded(it: Pair<Promise, Boolean>, p: Purchase) {
        if (!it.second) {
            it.first.resolve(wrap(p))
        } else {
            currentPurchaseListener = it.first
            currentPurchaseForAcknowledge = p
            acknowledge(p.sku, p.purchaseToken, it.first)
        }
    }

    override fun onPurchaseLoaded(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            purchasesMap[purchase.sku] = purchase
            purchaseListeners.remove(purchase.sku)?.let {
                acknowledgeIfNeeded(it, purchase)
            }
        }
    }

    override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onPurchaseLoadingFailed: $statusCode $description")
        // only one purchase process can be running at a time, so there will be single entry
        if (BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED == statusCode) {
            purchaseListeners.forEach {
                val purchase = purchasesMap[it.key]
                if (null != purchase) {
                    acknowledgeIfNeeded(it.value, purchase)
                } else {
                    it.value.first.reject(statusCode.toString(), description)
                }
            }
        } else {
            purchaseListeners.values.forEach { it.first.reject(statusCode.toString(), description) }
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
        Log.i(TAG, "Purchase was consumed")
    }

    override fun onPurchaseAcknowledged() {
        currentPurchaseListener?.let { promise ->
            currentPurchaseForAcknowledge?.let { promise.resolve(wrap(it)) }
        }
        currentPurchaseListener = null
        currentPurchaseForAcknowledge = null
        Log.i(TAG, "The purchase was acknowledged")
    }

    override fun onPurchaseConsumptionFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onPurchaseConsumptionFailed: $statusCode $description")
    }

    override fun onBillingClientError(statusCode: Int, description: String) {
        Log.e(TAG, "onBillingClientError: $statusCode $description")
        purchaseListeners.values.forEach{ it.first.reject(statusCode.toString(), description)}
        purchaseListeners.clear()
    }

    override fun onPurchaseAcknowledgeFailed(statusCode: Int, description: String) {
        Log.e(TAG, "onPurchaseAcknowledgeFailed: $statusCode $description")
    }

}