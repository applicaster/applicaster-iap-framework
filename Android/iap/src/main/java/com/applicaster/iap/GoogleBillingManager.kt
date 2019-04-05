package com.applicaster.iap

import android.app.Activity

/**
 * Main class for communication between billing library and app code
 */
object GoogleBillingManager: BillingManager {

    override fun initialize(callback: BillingListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun purchase(activity: Activity, purchaseItem: PurchaseItem, callback: BillingListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadPurchases(callback: BillingListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}