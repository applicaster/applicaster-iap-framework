# applicaster-iap-framework

This repository contains the code for the In-App Purchase framework for Android and iOS.

The In App Purchase framework is a mini sdk that provides all the necessary methods to communicate with the Apple App Store / Google Play store and to integrate in-app purchase flows into a mobile app.

The framework supports all types of products - Consumable, Non-Consumable, and Subscriptions.

The Android implementation of the framework is based on Google Play Billing Library 1.2.2 Release:
https://developer.android.com/google/play/billing/billing_library_releases_notes.html#release-1_2_2

The iOS Implementation of the framework is based of Apple's storekit in-app purchase library: https://developer.apple.com/documentation/storekit/in-app_purchase


## Getting Started

### Android

* Clone the project from github, cd to the Android folder, and open in Android Studio
* To setup billing and launch billing flow you must implement `BillingListener` and define all callbacks
* You must call `GoogleBillingHelper.init()` and pass your `BillingListener` implementation and Context before other SDK calls
* `GoogleBillingHelper.loadSkuDetails()`
* `GoogleBillingHelper.loadSkuDetailsForAllTypes()`
* `GoogleBillingHelper.purchase()`
* `GoogleBillingHelper.restorePurchses()`
* `GoogleBillingHelper.restorePurchasesForAllTypes()`
* `GoogleBillingHelper.consume()`

Detailed information on how to use framework API you can read in [Wiki](https://github.com/applicaster/applicaster-iap-framework/wiki).

### iOS

#### Installation

Add framework to `Podfile`:  
`pod 'ApplicasterIAP', :source => 'https://github.com/applicaster/applicaster-iap-framework'`  
Then run `pod install`

#### Usage

Import framework with `import ApplicasterIAP` inside `.swift` file.  
About framework API you can read in [Wiki](https://github.com/applicaster/applicaster-iap-framework/wiki)

## Related Links

Google Billing documentation: https://developer.android.com/google/play/billing/billing_overview

The iOS Implementation of the framework is based of Apple's storekit in-app purchase library: https://developer.apple.com/documentation/storekit/in-app_purchase

## Submit an Issue

For submitting issues and bug reports, please use the following link: https://github.com/applicaster/applicaster-iap-framework/issues/new/choose

## Code of Conduct

Please make sure to follow our code of conduct: https://developer.applicaster.com/Code-Of-Conduct.html
