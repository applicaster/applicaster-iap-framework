package com.applicaster.iap

import android.app.Activity

interface BillingManager {
    fun initialize(callback: BillingListener)
    fun purchase(activity: Activity, purchaseItem: PurchaseItem, callback: BillingListener)
    fun loadPurchases(callback: BillingListener)
}