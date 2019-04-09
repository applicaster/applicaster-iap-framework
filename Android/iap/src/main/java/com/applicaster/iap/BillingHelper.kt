package com.applicaster.iap

import android.app.Activity
import android.content.Context

interface BillingHelper {
    fun init(context: Context, callback: BillingListener)
    fun purchase(activity: Activity, purchaseItem: PurchaseItem)
    fun consume(purchaseItem: PurchaseItem)
    fun loadPurchases()
}