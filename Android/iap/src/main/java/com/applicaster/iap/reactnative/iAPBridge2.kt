package com.applicaster.quickbrickinplayer.reactnative

import com.applicaster.quickbrickinplayer.implementation.Accounts.InPlayerAuthorizationModelExt
import com.applicaster.quickbrickinplayer.implementation.InPlayerPlugin.Companion.inPlayerSDKInitialization
import com.applicaster.quickbrickinplayer.utils.noCredentialsError
import com.applicaster.quickbrickinplayer.utils.noExpectedPayloadParams
import com.applicaster.reactnative.utils.ConversionUtils
import com.facebook.react.bridge.*
import com.sdk.inplayer.callback.InPlayerCallback
import com.sdk.inplayer.configuration.InPlayer

enum class InPlayerAccountBridgeKeys(val shorthand: String) {
    FullName("fullName"),
    Email("username"),
    Password("password");
}

class InPlayerAccountBridge(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val bridgeName = "InPlayerAccountBridge"

    override fun getName(): String {
        return bridgeName
    }

    @ReactMethod
    fun signUp(payload: ReadableMap?,
               promise: Promise?) {
        try {
            val jsonObject = ConversionUtils.readableMapToJson(payload)
                    ?: throw IllegalArgumentException(noCredentialsError.message)
            inPlayerSDKInitialization(reactContext, jsonObject) == false && throw IllegalArgumentException(noCredentialsError.message)

            val fullName = jsonObject.getString(InPlayerAccountBridgeKeys.FullName.shorthand)
            val email = jsonObject.getString(InPlayerAccountBridgeKeys.Email.shorthand)
            val password = jsonObject.getString(InPlayerAccountBridgeKeys.Password.shorthand)

            if (fullName != null && email != null && password != null) {
                InPlayer.Account.signUp(fullName, email, password, password,InPlayerCallback {inPlayerUser, error ->
                    if (error == null) {
                        val inPlayerUserWrapper = InPlayerAuthorizationModelExt.wrapToMap(inPlayerUser)
                        promise?.resolve(inPlayerUserWrapper)
                    } else {
                        promise?.reject(JSApplicationCausedNativeException(error.errorsList.joinToString(), error.e))
                    }
                })
            } else {
                throw IllegalArgumentException(noExpectedPayloadParams.message + "fullName, email, password")
            }
        } catch (e: Exception) {
            promise?.reject(JSApplicationCausedNativeException(e.message, e))
        }
    }

    @ReactMethod
    fun authenticate(payload: ReadableMap?,
                     promise: Promise?) {
        try {
            val jsonObject = ConversionUtils.readableMapToJson(payload)
                    ?: throw IllegalArgumentException(noCredentialsError.message)
            inPlayerSDKInitialization(reactContext, jsonObject)

            val email = jsonObject.getString(InPlayerAccountBridgeKeys.Email.shorthand)
            val password = jsonObject.getString(InPlayerAccountBridgeKeys.Password.shorthand)

            if (email != null && password != null) {
                InPlayer.Account.authenticate(email, password, InPlayerCallback { inPlayerUser, error ->
                    if (error == null) {
                        val authorisationWrapper = InPlayerAuthorizationModelExt.wrapToMap(inPlayerUser)
                        promise?.resolve(authorisationWrapper)
                    } else {
                        promise?.reject(JSApplicationCausedNativeException(error.errorsList.joinToString(), error.e))
                    }
                })
            } else {
                throw IllegalArgumentException(noExpectedPayloadParams.message + "email, password")
            }
        } catch (e: Exception) {
            promise?.reject(JSApplicationCausedNativeException(e.message, e))
        }
    }


    @ReactMethod
    fun isAuthenticated(payload: ReadableMap?, promise: Promise?) {
        try {
            val jsonObject = ConversionUtils.readableMapToJson(payload)
                    ?: throw IllegalArgumentException(noCredentialsError.message)
            inPlayerSDKInitialization(reactContext, jsonObject)
            promise?.resolve(InPlayer.Account.isAuthenticated())
        } catch (e: Exception) {
            promise?.reject(JSApplicationCausedNativeException(e.message, e))
        }
    }

    @ReactMethod
    fun signOut(payload: ReadableMap?, promise: Promise?) {
        try {
            val jsonObject = ConversionUtils.readableMapToJson(payload)
                    ?: throw IllegalArgumentException(noCredentialsError.message)
            inPlayerSDKInitialization(reactContext, jsonObject)
            InPlayer.Account.signOut(InPlayerCallback { result, error ->
                if (error == null) {
                    promise?.resolve(true)
                } else {
                    promise?.reject(JSApplicationCausedNativeException(error.errorsList.joinToString(), error.e))
                }
            })
        } catch (e: java.lang.Exception) {
            promise?.reject(JSApplicationCausedNativeException(e.message, e))
        }
    }
}