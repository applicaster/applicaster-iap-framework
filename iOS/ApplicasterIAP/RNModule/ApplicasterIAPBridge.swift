import Foundation
import React
import StoreKit

struct ApplicasterIAPBridgeErrors {
    static let generalError = "ApplicasterIAPBridgeError"
    static let noProducts = "Products Ids do not exist"
    static let canNotFindProduct = "Can not find availible identifier:"
}

@objc(ApplicasterIAPBridge)
class ApplicasterIAPBridge: NSObject, RCTBridgeModule {
    static var availibleProducts: Set<SKProduct> = []
    static func moduleName() -> String! {
        return "ApplicasterIAPBridge"
    }

    public class func requiresMainQueueSetup() -> Bool {
        return true
    }

    /// prefered thread on which to run this native module
    @objc public var methodQueue: DispatchQueue {
        return DispatchQueue.main
    }

    @objc func products(_ products: Set<String>?,
                        resolver: @escaping RCTPromiseResolveBlock,
                        rejecter: @escaping RCTPromiseRejectBlock) {
        guard let products = products else {
            rejecter(ApplicasterIAPBridgeErrors.generalError,
                     ApplicasterIAPBridgeErrors.noProducts,
                     nil)
            return
        }

        BillingHelper.sharedInstance.products(products) { (result: ProductsQueryResult) in
            switch result {
            case let .success(response):
                let products = response.products

                products.forEach({ ApplicasterIAPBridge.availibleProducts.insert($0) })

                resolver(["products": SKProduct.wrappProducts(products: products),
                          "invalidIDs": response.invalidIDs])
            case let .failure(error):
                rejecter(ApplicasterIAPBridgeErrors.generalError,
                         error.localizedDescription,
                         error)
            }
        }
    }

    @objc func purchase(_ productIdentifier: String?,
                        resolver: @escaping RCTPromiseResolveBlock,
                        rejecter: @escaping RCTPromiseRejectBlock) {
        guard let productIdentifier = productIdentifier,
            let product = ApplicasterIAPBridge.retrieveAvailableProduct(from: productIdentifier) else {
            rejecter(ApplicasterIAPBridgeErrors.generalError,
                     ApplicasterIAPBridgeErrors.canNotFindProduct,
                     nil)
            return
        }
        BillingHelper.sharedInstance.purchase(product) { (result: PurchaseResult) in
            switch result {
            case let .success(purchase):
                let purchaseDict = purchase.toDictionary()
                Utils.retrieveReciept { reciept in
                    resolver([
                        "reciept": reciept as Any,
                        "purchase": purchaseDict]
                    )
                }

            case let .failure(error):
                rejecter(ApplicasterIAPBridgeErrors.generalError,
                         error.localizedDescription,
                         error)
            }
        }
    }

    @objc func restore(_ resolver: @escaping RCTPromiseResolveBlock,
                       rejecter: @escaping RCTPromiseRejectBlock) {
        BillingHelper.sharedInstance.restore { (result: RestoreResult) in
            switch result {
            case let .success(transactions):
                let purchasedItemIDs = transactions.map({ $0.payment.productIdentifier })
                resolver(purchasedItemIDs)
            case let .failure(error):
                rejecter(ApplicasterIAPBridgeErrors.generalError,
                         error.localizedDescription,
                         error)
            }
        }
    }

    class func retrieveAvailableProduct(from productIdentifier: String) -> SKProduct? {
        return availibleProducts.first { (product) -> Bool in
            product.productIdentifier == productIdentifier
        }
    }
}
