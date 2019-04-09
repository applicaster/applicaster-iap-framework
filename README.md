# applicaster-iap-framework

This repository contains the code for the IAP SDK (Android, iOS)

## Getting Started


### Android

* Clone the project from github, cd to the Android folder, and open in Android Studio
* To setup billing and launch billing flow you must implement `BillingListener` and define all callbacks
* You must call `GoogleBillingHelper.init()` and pass your `BillingListener` implementation and Context before other SDK calls
* `GoogleBillingHelper.purchase()`
* `GoogleBillingHelper.loadpurchses()`
* `GoogleBillingHelper.consume()`

### iOS


## Related Links

IAP SDK wiki: https://github.com/applicaster/applicaster-iap-framework/wiki

Google Billing documentation: https://developer.android.com/google/play/billing/billing_overview

## Submit an Issue

## Code of Conduct
