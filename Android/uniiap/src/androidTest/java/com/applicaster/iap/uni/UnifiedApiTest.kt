package com.applicaster.iap.uni


import android.util.Log
import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.applicaster.iap.uni.api.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Semaphore


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UnifiedApiTest {

    @Test
    fun useAppContext() {

        class TestListener(private var message: String) : IAPListener {

            private var event:Semaphore? = null

            public var skus: List<Sku>? = null
            public var purchases: List<Purchase>? = null

            fun waitForEvent() {
                event = Semaphore(1)
                event?.acquire()
            }

            fun waitForResult() {
                event?.acquire()
                event?.release()
                event = null
            }

            private fun onResult(result: String) {
                Log.d("GooglePlayTest", "$message: $result")
                event?.release()
            }

//            override fun onPurchaseLoaded(purchases: List<Purchase>) {
//                this.purchases = purchases
//                onResult("onPurchaseLoaded: ${purchases.size}")
//            }
//
//            override fun onPurchaseLoadingFailed(statusCode: Int, description: String) {
//                onResult("onPurchaseLoadingFailed: $statusCode $description")
//            }

            override fun onPurchasesRestored(purchases: List<Purchase>) {
                this.purchases = purchases
                onResult("onPurchaseLoaded: ${purchases.size}")
            }

            override fun onSkuDetailsLoaded(skuDetails: List<Sku>) {
                this.skus = skuDetails
                onResult("onPurchaseLoaded: ${skuDetails.size}")
            }

            override fun onSkuDetailsLoadingFailed(
                result: IBillingAPI.IAPResult,
                description: String
            ) {
                onResult("onPurchaseLoadingFailed: $result $description")
            }

            override fun onPurchased(purchase: Purchase) {
                this.purchases = listOf(purchase)
                onResult("onPurchased: ${purchase.productIdentifier}")
            }

            override fun onPurchaseFailed(result: IBillingAPI.IAPResult, description: String) {
                TODO("Not yet implemented")
            }

            override fun onPurchaseRestoreFailed(
                result: IBillingAPI.IAPResult,
                description: String
            ) {
                onResult("onPurchaseRestoreFailed: $result $description")
            }

            override fun onPurchaseConsumed(purchaseToken: String) {
                onResult("onPurchaseConsumed: $purchaseToken")
            }

            override fun onPurchaseConsumptionFailed(
                result: IBillingAPI.IAPResult,
                description: String
            ) {
                onResult("onPurchaseConsumptionFailed: $result $description")
            }

            override fun onPurchaseAcknowledgeFailed(
                result: IBillingAPI.IAPResult,
                description: String
            ) {
                onResult("onPurchaseAcknowledgeFailed: $result $description")
            }

            override fun onPurchaseAcknowledged() {
                onResult("onPurchaseAcknowledged")
            }

            override fun onBillingClientError(result: IBillingAPI.IAPResult, description: String) {
                onResult("onBillingClientError: $result $description")
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("Need specific package name","com.applicaster.inplayerdemo", appContext.packageName)

        val api = IBillingAPI.create(IBillingAPI.Vendor.play)
        api.init(appContext)

        // Load SKUs
        val skuLoaded = TestListener("loadSkuDetails")
        skuLoaded.waitForEvent()
        api.loadSkuDetails(IBillingAPI.SkuType.consumable, arrayListOf("73149_17806"), skuLoaded)
        skuLoaded.waitForResult()

        // Try to purchase something
        val scenario = launchActivity<TestActivity>()
        val purchaseListener = TestListener("purchaseListener")
        scenario.onActivity { activity ->
            purchaseListener.waitForEvent()
            api.purchase(activity, PurchaseRequest(skuLoaded.skus!!.get(0).productIdentifier, false), purchaseListener)
        }
        purchaseListener.waitForResult() // wait outside onActivity!
        scenario.close()
    }
}