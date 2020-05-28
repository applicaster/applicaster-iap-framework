package com.applicaster.iap.reactnative.utils;

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.facebook.react.bridge.WritableNativeMap

// should I make these Extensions?

fun wrap(skuDetails: SkuDetails): WritableNativeMap {
    val skuDetail = WritableNativeMap()
    skuDetail.putString("productIdentifier", skuDetails.sku)
    skuDetail.putString("priceLocale", skuDetails.price)
    skuDetail.putString("localizedTitle", skuDetails.title)
    skuDetail.putString("localizedDescription", skuDetails.description)
    return skuDetail
}

fun wrap(purchase: Purchase): WritableNativeMap {
    val nativeMap = WritableNativeMap()
    nativeMap.putString("productIdentifier", purchase.sku)
    nativeMap.putString("receipt", purchase.originalJson)
    return nativeMap
}
