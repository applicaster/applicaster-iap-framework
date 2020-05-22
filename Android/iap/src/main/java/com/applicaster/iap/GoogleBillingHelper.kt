package com.applicaster.iap

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.applicaster.iap.security.Security

/**
 * Main class for communication between billing library and app code
 */
object GoogleBillingHelper : BillingHelper {

    private val TAG = GoogleBillingHelper::class.java.simpleName

    // Error description fields
    private const val USER_CANCELED_DESC = "User canceled the request that is currently taking place"
    private const val BILLING_UNAVAILABLE_DESC = "Version for the Billing API is not supported for the requested type"
    private const val DEVELOPER_ERROR_DESC = "Incorrect arguments have been sent to the Billing API"
    private const val ERROR_DESC = "Error occurs during the API action being executed"
    private const val FEATURE_NOT_SUPPORTED_DESC = "Requested action is not supported by play services on the current device"
    private const val ITEM_ALREADY_OWNED_DESC = "Attempt to purchases an item that user already owns"
    private const val ITEM_NOT_OWNED_DESC = "Attempt to consume an item that user does not currently own"
    private const val ITEM_UNAVAILABLE_DESC = "Attempt to purchase a product that is not available for purchase"
    private const val SERVICE_DISCONNECTED_DESC = "Play service is not connected at the point of the request"
    private const val SERVICE_TIMEOUT_DESC = "The request has reached the maximum timeout before Google Play responds"
    private const val SERVICE_UNAVAILABLE_DESC = "Error occurs in relation to the devices network connectivity"
    private const val UNDEFINED_ERROR_DESC = "Undefined error"
    private const val BLLING_SERVICE_CONNECTION_WAS_LOST = "Billing service connection was lost"

    private var connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
    private lateinit var billingListener: BillingListener
    private lateinit var billingClient: BillingClient

    private var isMultitypeQueryInProgress: Boolean = false
    private val cachedSkuDetails = arrayListOf<SkuDetails>()

    override fun init(applicationContext: Context, callback: BillingListener) {
        billingListener = callback

        val billingClientBuilder = with(BillingClient.newBuilder(applicationContext)) {
            // set PurchaseUpdatedListener to BillingClient.Builder
            setListener { responseCode, purchases ->
                updatePurchases(responseCode, purchases)
            }
        }

        //build configured builder and set result to Billing client instance
        billingClient = billingClientBuilder.build()
    }

    override fun purchase(activity: Activity, skuDetails: SkuDetails) {
        executeFlow { startPurchaseFlow(activity, skuDetails) }
    }

    override fun loadSkuDetails(@BillingClient.SkuType skuType: String,
                                skusList: List<String>,
                                callback: BillingListener?) {
        executeFlow( { querySkuDetails(skuType, skusList, callback) }, callback)
    }

    override fun loadSkuDetailsForAllTypes(skus: Map<String, String>,
                                           callback: BillingListener?) {
        // filter items by SkuType
        val inapps = skus.filterValues {
                skuType -> skuType == BillingClient.SkuType.INAPP
        }.map { it.key }

        val subs = skus.filterValues {
                skuType -> skuType == BillingClient.SkuType.SUBS
        }.map { it.key }

        //clear cached sku details before querySkuDetails call
        cachedSkuDetails.clear()

        // execute flow depends on filtered items by SkuType
        executeFlow {
            when {
                subs.isNotEmpty() && inapps.isEmpty() -> {
                    querySkuDetails(BillingClient.SkuType.SUBS, subs, callback)
                }

                subs.isEmpty() && inapps.isNotEmpty() -> {
                    querySkuDetails(BillingClient.SkuType.INAPP, inapps, callback)
                }

                subs.isNotEmpty() && inapps.isNotEmpty() -> {
                    isMultitypeQueryInProgress = true
                    querySkuDetails(BillingClient.SkuType.SUBS, subs, callback)
                    querySkuDetails(BillingClient.SkuType.INAPP, inapps, callback)
                }
            }
        }
    }

    override fun restorePurchases(@BillingClient.SkuType skuType: String) {
        executeFlow { billingListener.onPurchasesRestored(queryPurchases(skuType)) }
    }

    override fun restorePurchasesForAllTypes(callback: BillingListener?) {
        executeFlow {
            val restoredSubscriptions: List<Purchase> = queryPurchases(BillingClient.SkuType.SUBS)
            val restoredInapps: List<Purchase> = queryPurchases(BillingClient.SkuType.INAPP)
            billingListener.onPurchasesRestored(restoredSubscriptions + restoredInapps)
            callback?.onPurchasesRestored(restoredSubscriptions + restoredInapps)
        }
    }

    override fun consume(purchaseItem: Purchase) {
        executeFlow { consumeAsync(purchaseItem.purchaseToken) }
    }

    override fun validatePurchases(appPublicKey: String, purchases: List<Purchase>) {
        // we should check app public key if it's not empty and return empty list if it does
        if (appPublicKey.isEmpty()) {
            Log.w(TAG, "App public key should be not empty!")
            billingListener.onPurchaseLoaded(emptyList())
        } else {
            val validPurchases: List<Purchase> = purchases.filter {
                Security.verifyPurchase(appPublicKey, it.originalJson, it.purchaseToken)
            }
            billingListener.onPurchaseLoaded(validPurchases)
        }
    }

    private fun executeFlow(function: () -> Unit) {
        executeFlow(function, null)
    }

    // Check connection status and run block passed to this function
    // If the connection wasn't established try to establish connection and
    // execute passed block after establishing the connection
    private fun executeFlow(function: () -> Unit, callback: BillingListener?) {
        if (connectionStatus == ConnectionStatus.CONNECTED && billingClient.isReady) {
            // execute given function immediately
            function()
        } else {
            // start the connection if not connected already and if success call function
            startConnection(function, callback)
        }
    }

    //update handle response code and update purchases if ResponseCode.OK
    private fun updatePurchases(@BillingClient.BillingResponse responseCode: Int, purchases: List<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                billingListener.onPurchaseLoaded(purchases ?: arrayListOf())
            }
            else -> {
                billingListener.onPurchaseLoadingFailed(
                    responseCode,
                    handleErrorResult(responseCode)
                )
            }
        }
    }


    // start Google billing service connection
    private fun startConnection(function: () -> Unit, callback: BillingListener?) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
                connectionStatus = ConnectionStatus.DISCONNECTED
                billingListener.onBillingClientError(
                    BillingClient.BillingResponse.SERVICE_DISCONNECTED,
                    BLLING_SERVICE_CONNECTION_WAS_LOST
                )
                callback?.onBillingClientError(
                        BillingClient.BillingResponse.SERVICE_DISCONNECTED,
                        BLLING_SERVICE_CONNECTION_WAS_LOST
                )
            }

            override fun onBillingSetupFinished(@BillingClient.BillingResponse responseCode: Int) {
                Log.i(TAG, "Billing setup finished")

                when (responseCode) {
                    BillingClient.BillingResponse.OK -> {
                        connectionStatus = ConnectionStatus.CONNECTED
                        function()
                    }
                    else -> {
                        connectionStatus = ConnectionStatus.DISCONNECTED
                        billingListener.onBillingClientError(responseCode, handleErrorResult(responseCode))
                        callback?.onBillingClientError(
                                BillingClient.BillingResponse.SERVICE_DISCONNECTED,
                                BLLING_SERVICE_CONNECTION_WAS_LOST
                        )
                    }
                }
            }
        })
    }

    private fun queryPurchases(@BillingClient.SkuType skuType: String): List<Purchase> {
        val result = billingClient.queryPurchases(skuType)
        //if result isn't null set to callback purchases list else set empty list
        return result?.purchasesList ?: listOf()
    }

    private fun querySkuDetails(skuType: String, skusList: List<String>, callback: BillingListener?) {

        val skuDetailsParams: SkuDetailsParams =
            SkuDetailsParams.newBuilder()
                .setType(skuType)
                .setSkusList(skusList)
                .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { responseCode, skuDetailsList ->
            when (responseCode) {
                BillingClient.BillingResponse.OK -> {
                    //call callback function with result depends on billing flow state
                    if (isMultitypeQueryInProgress) {
                        // save obtained items to the cache and set billing flow to finished(isMultitypeQueryInProgress = true)
                        cachedSkuDetails.addAll(skuDetailsList)
                        isMultitypeQueryInProgress = false
                    } else {
                        billingListener.onSkuDetailsLoaded(cachedSkuDetails + skuDetailsList)
                        callback?.onSkuDetailsLoaded(cachedSkuDetails + skuDetailsList)
                        cachedSkuDetails.clear()
                    }
                }
                else -> {
                    isMultitypeQueryInProgress = false
                    cachedSkuDetails.clear()
                    billingListener.onSkuDetailsLoadingFailed(responseCode, handleErrorResult(responseCode))
                    callback?.onSkuDetailsLoadingFailed(responseCode, handleErrorResult(responseCode))
                }
            }
        }
    }

    private fun startPurchaseFlow(activity: Activity, skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams
            .newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    private fun consumeAsync(purchaseToken: String) {
        billingClient.consumeAsync(purchaseToken) { responseCode, outToken ->
            when (responseCode) {
                BillingClient.BillingResponse.OK -> {
                    billingListener.onPurchaseConsumed(outToken)
                }
                else -> {
                    billingListener.onPurchaseConsumptionFailed(
                        responseCode,
                        handleErrorResult(responseCode)
                    )
                }
            }
        }
    }

    private fun handleErrorResult(@BillingClient.BillingResponse responseCode: Int): String {
        return when (responseCode) {
            BillingClient.BillingResponse.USER_CANCELED -> {
                Log.w(TAG, USER_CANCELED_DESC)
                USER_CANCELED_DESC
            }
            BillingClient.BillingResponse.BILLING_UNAVAILABLE -> {
                Log.e(TAG, BILLING_UNAVAILABLE_DESC)
                BILLING_UNAVAILABLE_DESC
            }
            BillingClient.BillingResponse.DEVELOPER_ERROR -> {
                Log.e(TAG, DEVELOPER_ERROR_DESC)
                DEVELOPER_ERROR_DESC
            }
            BillingClient.BillingResponse.ERROR -> {
                Log.e(TAG, ERROR_DESC)
                ERROR_DESC
            }
            BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED -> {
                Log.e(TAG, FEATURE_NOT_SUPPORTED_DESC)
                FEATURE_NOT_SUPPORTED_DESC
            }
            BillingClient.BillingResponse.ITEM_ALREADY_OWNED -> {
                Log.w(TAG, ITEM_ALREADY_OWNED_DESC)
                ITEM_ALREADY_OWNED_DESC
            }
            BillingClient.BillingResponse.ITEM_NOT_OWNED -> {
                Log.w(TAG, ITEM_NOT_OWNED_DESC)
                ITEM_NOT_OWNED_DESC
            }
            BillingClient.BillingResponse.ITEM_UNAVAILABLE -> {
                Log.w(TAG, ITEM_UNAVAILABLE_DESC)
                ITEM_UNAVAILABLE_DESC
            }
            BillingClient.BillingResponse.SERVICE_DISCONNECTED -> {
                Log.e(TAG, SERVICE_DISCONNECTED_DESC)
                SERVICE_DISCONNECTED_DESC
            }
            BillingClient.BillingResponse.SERVICE_TIMEOUT -> {
                Log.e(TAG, SERVICE_TIMEOUT_DESC)
                SERVICE_TIMEOUT_DESC
            }
            BillingClient.BillingResponse.SERVICE_UNAVAILABLE -> {
                Log.e(TAG, SERVICE_UNAVAILABLE_DESC)
                SERVICE_UNAVAILABLE_DESC
            }
            else -> {
                Log.e(TAG, UNDEFINED_ERROR_DESC)
                UNDEFINED_ERROR_DESC
            }
        }
    }

    private enum class ConnectionStatus {
        CONNECTED, DISCONNECTED
    }
}