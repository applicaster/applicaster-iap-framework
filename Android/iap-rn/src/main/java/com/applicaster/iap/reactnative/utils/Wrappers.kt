package com.applicaster.iap.reactnative.utils;

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.facebook.react.bridge.WritableNativeMap

// should I make these Extensions?

fun wrap(skuDetails: SkuDetails): WritableNativeMap {
    val skuDetail = WritableNativeMap()
    skuDetail.putString("productIdentifier", skuDetails.sku)
    skuDetail.putString("price", skuDetails.price)
    skuDetail.putString("title", skuDetails.title)
    skuDetail.putString("description", skuDetails.description)
    return skuDetail
}

fun wrap(purchase: Purchase): WritableNativeMap {
    val nativeMap = WritableNativeMap()
    nativeMap.putString("productIdentifier", purchase.sku)
    nativeMap.putString("transactionIdentifier", purchase.purchaseToken)
    nativeMap.putString("receipt", purchase.originalJson)
    return nativeMap
}

fun unwrapProductIdentifier(map: HashMap<String, String>): Pair<String, String> {
    return Pair(
            map["productIdentifier"]!!,
            when (map["productType"]!!) {
                "subscription" -> BillingClient.SkuType.SUBS
                "nonConsumable" -> BillingClient.SkuType.INAPP
                "consumable" -> BillingClient.SkuType.INAPP
                else -> throw IllegalArgumentException("Unknown SKU type ${map["productType"]}")
            })
}
