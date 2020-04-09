import Foundation
import StoreKit

struct SKProductWrapperKeys {
    static let localizedDescription = "localizedDescription"
    static let localizedTitle = "localizedTitle"
    static let price = "price"
    static let priceLocale = "priceLocale"
    static let productIdentifier = "productIdentifier"
    static let isDownloadable = "isDownloadable"
    static let downloadContentLengths = "downloadContentLengths"
    static let contentVersion = "contentVersion"
    static let downloadContentVersion = "downloadContentVersion"
    static let subscriptionPeriod = "subscriptionPeriod"
}

extension SKProduct {
    class func wrappProducts(products: [SKProduct]) -> [[String: Any]] {
        return products.map({ wrappProductToDictionary(product: $0) })
    }

    class func wrappProductToDictionary(product: SKProduct) -> [String: Any] {
        var retVal: [String: Any] = [:]

        retVal[SKProductWrapperKeys.localizedDescription] = product.localizedDescription
        retVal[SKProductWrapperKeys.localizedTitle] = product.localizedTitle
        retVal[SKProductWrapperKeys.price] = product.price
        retVal[SKProductWrapperKeys.priceLocale] = product.priceLocale
        retVal[SKProductWrapperKeys.productIdentifier] = product.productIdentifier
        retVal[SKProductWrapperKeys.isDownloadable] = product.isDownloadable
        retVal[SKProductWrapperKeys.downloadContentLengths] = product.downloadContentLengths
        retVal[SKProductWrapperKeys.contentVersion] = product.contentVersion
        retVal[SKProductWrapperKeys.downloadContentVersion] = product.downloadContentVersion

        return retVal
    }
}

