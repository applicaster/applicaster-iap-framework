package com.applicaster.iap

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface BillingHelper {

    /**
     * Initialises billing client and sets up the billing listener
     *
     * @param applicationContext An application context to bind to the in-app billing service
     * @param callback A listener that will be called as a result of call a function from billing helper
     *
     * @see BillingListener
     * @see BillingHelper
     */
    fun init(applicationContext: Context, callback: BillingListener)

    /**
     * Loads list of SkuDetails that and calls [BillingListener.onSkuDetailsLoaded]
     * or [BillingListener.onSkuDetailsLoadingFailed] if operation was failed
     *
     * @param skuType A type of the SKU: in-app or subscription
     * @param skusList A list of sku IDs
     */
    fun loadSkuDetails(@BillingClient.SkuType skuType: String, skusList: List<String>)

    /**
     * Get purchases details for all the items bought within app.
     *
     * @param skuType A type of the SKU: in-app or subscription
     */
    fun loadPurchases(@BillingClient.SkuType skuType: String)

    /**
     * Initiate the billing flow for an in-app purchase or subscription.
     *
     * @param activity An activity reference from which the billing flow will be launched.
     * @param skuDetails In-app product's or subscription's details
     */
    fun purchase(activity: Activity, skuDetails: SkuDetails)

    /**
     * Consumes a given in-app product. As a result of consumption, the user will no longer own it.
     *
     * @param purchaseItem Item that represents in-app billing purchase.
     */
    fun consume(purchaseItem: Purchase)
}