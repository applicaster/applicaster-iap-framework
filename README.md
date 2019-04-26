# applicaster-iap-framework

This repository contains the code for the IAP SDK (Android, iOS)

## Getting Started

### Android

* Clone the project from github, cd to the Android folder, and open in Android Studio
* To setup billing and launch billing flow you must implement `BillingListener` and define all callbacks
* You must call `GoogleBillingHelper.init()` and pass your `BillingListener` implementation and Context before other SDK calls
* `GoogleBillingHelper.loadSkuDetails()`
* `GoogleBillingHelper.purchase()`
* `GoogleBillingHelper.loadpurchses()`
* `GoogleBillingHelper.consume()`
Detailed information on how to use framework API you can read in [Wiki](https://github.com/applicaster/applicaster-iap-framework/wiki).

### iOS

#### Installation

Add framework to `Podfile`:  
`pod 'IAP', :source => 'https://github.com/applicaster/applicaster-iap-framework'`  
Then run `pod install`

#### Usage

Import framework with `import IAP` inside `.swift` file.  
About framework API you can read in [Wiki](https://github.com/applicaster/applicaster-iap-framework/wiki
)

## Related Links

Google Billing documentation: https://developer.android.com/google/play/billing/billing_overview

## Submit an Issue

## Code of Conduct
