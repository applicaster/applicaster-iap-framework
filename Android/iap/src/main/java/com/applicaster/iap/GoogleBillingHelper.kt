package com.applicaster.iap

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

/**
 * Main class for communication between billing library and app code
 */
object GoogleBillingHelper : BillingHelper {

    private val TAG = GoogleBillingHelper::class.java.simpleName

    private var connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED

    private lateinit var billingListener: BillingListener
    private lateinit var billingClient: BillingClient

    override fun init(context: Context, callback: BillingListener) {
        billingListener = callback

        val billingClientBuilder = with(BillingClient.newBuilder(context)) {
            // set PurchaseUpdatedListener to BillingClient.Builder
            setListener { responseCode, purchases ->
                updatePurchases(responseCode, purchases)
            }
        }

        //build configured builder and set result to Billing client instance
        billingClient = billingClientBuilder.build()
    }

    override fun purchase(activity: Activity, purchaseItem: PurchaseItem) {
        executeFlow { startPurchaseFlow(activity, purchaseItem) }
    }

    override fun loadPurchases() {
        executeFlow { querySkuDetails("", ArrayList()) }
    }

    // Check connection status and run function passed to this function
    // If connection doesn't established try to establish connection and
    // execute passed function after establishing the connection
    private fun executeFlow(function: () -> Unit) {
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            // execute given function immediately
            function()
        } else {
            startConnection(function)
        }
    }

    //update handle response code and update purchases if ResponseCode.OK
    private fun updatePurchases(@BillingClient.BillingResponse responseCode: Int, purchases: List<Purchase>?) {
        // need transform each Purchase to PurchaseItem?
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                //add received purchases to list
            }

            BillingClient.BillingResponse.USER_CANCELED -> {
                Log.w(TAG, "Purchase was cancelled by user")
            }

            else -> {
                //TODO: implement error result
            }
        }
    }


    // start Google billing service connection
    private fun startConnection(function: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
                //TODO: we should try to refresh connection several times.
                connectionStatus = ConnectionStatus.DISCONNECTED
            }

            override fun onBillingSetupFinished(@BillingClient.BillingResponse responseCode: Int) {
                Log.i(TAG, "Billing setup finished")

                when (responseCode) {
                    BillingClient.BillingResponse.OK -> {
                        connectionStatus = ConnectionStatus.CONNECTED
                        function()
                    }
                    else -> { /*//TODO: handle error result*/ }
                }

            }
        })
    }

    private fun querySkuDetails(skuType: String, skusList: List<String>) {

        val skuDetailsParams: SkuDetailsParams =
            SkuDetailsParams.newBuilder()
                .setType(skuType)
                .setSkusList(skusList)
                .build()

        with(billingClient) {
            querySkuDetailsAsync(skuDetailsParams) { responseCode, skuDetailsList ->
                when (responseCode) {
                    BillingClient.BillingResponse.OK -> {
                        //TODO: create list of PurchaseItem from skuDetailsList
                        //call callback function with result
                        billingListener.onPurcaseLoaded(/*pass PurchaseItem list here*/arrayListOf())
                    }

                    else -> {
                        Log.w(TAG, "Billing response error")
                    }
                }
            }
        }
    }

    private fun startPurchaseFlow(activity: Activity, purchaseItem: PurchaseItem) {
        val flowParams = BillingFlowParams
            .newBuilder()
            .setSkuDetails(/*//TODO:replace with SkuDetails was got from PurchaseItem*/SkuDetails(""))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun consume(purchaseItem: PurchaseItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    enum class ConnectionStatus {
        CONNECTED, DISCONNECTED
    }
}