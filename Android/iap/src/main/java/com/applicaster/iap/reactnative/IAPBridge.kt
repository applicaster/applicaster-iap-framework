package com.applicaster.quickbrickinplayer.reactnative

import com.applicaster.quickbrickinplayer.implementation.Accounts.InPlayerAuthorizationModelExt
import com.applicaster.quickbrickinplayer.implementation.Assets.InPlayerItemAccessExt
import com.applicaster.quickbrickinplayer.implementation.InPlayerPlugin.Companion.inPlayerSDKInitialization
import com.applicaster.quickbrickinplayer.utils.noCredentialsError
import com.applicaster.quickbrickinplayer.utils.noExpectedPayloadParams
import com.applicaster.reactnative.utils.ConversionUtils
import com.facebook.react.bridge.*
import com.sdk.inplayer.callback.InPlayerCallback
import com.sdk.inplayer.configuration.InPlayer

enum class InPlayerAssetBridgeKeys(val shorthand: String) {
    Id("id"),
    EntryId("entryId");
}

class InPlayerAssetBridge(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val bridgeName = "InPlayerAssetBridge"

    override fun getName(): String {
        return bridgeName
    }

    @ReactMethod
    fun checkAccessForAsset(payload: ReadableMap?,
                            promise: Promise?) {
        try {
            val jsonObject = ConversionUtils.readableMapToJson(payload)
                    ?: throw IllegalArgumentException(noCredentialsError.message)
            val id = jsonObject.getInt(InPlayerAssetBridgeKeys.Id.shorthand)

            !inPlayerSDKInitialization(reactContext, jsonObject) && throw IllegalArgumentException(noCredentialsError.message)


            val entryId = jsonObject.getString(InPlayerAssetBridgeKeys.EntryId.shorthand)

            InPlayer.Assets.checkAccessForAsset(id, entryId,InPlayerCallback {itemAccess, error ->
                if (error == null) {
                    val inPlayerUserWrapper = InPlayerItemAccessExt.wrapToMap(itemAccess)
                    promise?.resolve(inPlayerUserWrapper)
                } else {
                    promise?.reject(JSApplicationCausedNativeException(error.errorsList.joinToString(), error.e))
                }
            })

        } catch (e: Exception) {
            promise?.reject(JSApplicationCausedNativeException(e.message, e))
        }
    }


}