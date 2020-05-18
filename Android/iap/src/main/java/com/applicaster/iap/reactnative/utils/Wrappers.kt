package com.applicaster.iap.reactnative.utils;

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.facebook.react.bridge.WritableNativeMap

// todo: remove this
fun mapID(identifier: String) =
        when (identifier) {
            "72348_17073" -> "monthly"
            "72348_17074" -> "yearly"
            else -> identifier
        }

// todo: remove this
fun rmapID(identifier: String) =
        when (identifier) {
            "monthly" -> "72348_17073"
            "yearly" -> "72348_17074"
            else -> identifier
        }

// should I make these Extensions?

fun wrap(skuDetails: SkuDetails): WritableNativeMap {
    val skuDetail = WritableNativeMap()
    skuDetail.putString("productIdentifier", rmapID(skuDetails.sku))
    skuDetail.putString("price", skuDetails.price)
    skuDetail.putString("localizedTitle", skuDetails.title)
    skuDetail.putString("localizedDescription", skuDetails.description)
    return skuDetail
}

fun wrap(purchase: Purchase): WritableNativeMap {
    val nativeMap = WritableNativeMap()
    nativeMap.putString("productIdentifier", purchase.sku)
    nativeMap.putString("receipt", purchase.purchaseToken)
    return nativeMap
}
