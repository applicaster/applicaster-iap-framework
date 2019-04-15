//
//  BillingHelper.swift
//  IAP
//
//  Created by Roman Karpievich on 4/9/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation
import StoreKit

class BillingHelper {
    
    public static let sharedInstance = BillingHelper()
    
    private let storeObserver = StoreObserver()
    
    private var productsQuery: ProductsQuery?
    private var receiptRefreshQuery: ReceiptRefreshQuery?
    
    private init() {
        
    }
    
    // MARK: - Public methods
    
    public func products(_ identifiers: Set<String>,
                         completion: @escaping (ProductsQueryResult) -> Void) {
        productsQuery = ProductsQuery(identifiers: identifiers) { [weak self] (result) in
            self?.productsQuery = nil
            completion(result)
        }
        
        productsQuery?.start()
    }
    
    public func canMakePayments() -> Bool {
        return SKPaymentQueue.canMakePayments()
    }
    
    public func purchase(_ item: SKProduct, amount: Int = 1, atomic: Bool = true, completion: @escaping (PurchaseResult) -> Void) {
        let purchase = Purchase(item: item,
                                amount: amount,
                                atomic: atomic)
        storeObserver.buy(purchase, completion: completion)
    }
    
    public func restore(completion: @escaping (RestoreResult) -> Void) {
        storeObserver.restore(completion: completion)
    }
    
    public func localReceiptData() -> Data? {
        guard let localReceiptURL = Bundle.main.appStoreReceiptURL else {
            return nil
        }
        
        let data = try? Data(contentsOf: localReceiptURL)
        return data
    }
    
    public func refreshReceipt(receiptProperties: [String: Any]? = nil,
                               completion: @escaping (ReceiptRefreshResult) -> Void) {
        receiptRefreshQuery = ReceiptRefreshQuery(receiptProperties: receiptProperties,
                                                  completion: { [weak self] (result) in
            self?.receiptRefreshQuery = nil
            completion(result)
        })
        
        receiptRefreshQuery?.start()
    }
    
    // MARK: - Private methods

    
}
