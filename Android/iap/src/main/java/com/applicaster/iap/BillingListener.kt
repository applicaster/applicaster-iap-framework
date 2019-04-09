package com.applicaster.iap

/**
 * Callback for general billing events
 */
interface BillingListener {
    fun onPurchaseFailed(/*status code?*/)
    fun onPurcaseLoaded(purchases: List<PurchaseItem>)
}