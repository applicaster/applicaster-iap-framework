//
//  StoreObserver.swift
//  IAP
//
//  Created by Roman Karpievich on 4/11/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation
import StoreKit

class StoreObserver: NSObject, SKPaymentTransactionObserver {
    
    typealias PurchaseCompletion = (PurchaseResult) -> Void
    typealias RestoreCompletion = (RestoreResult) -> Void
    
    private var activePurchases: [String: (Purchase, PurchaseCompletion)] = [:]
    private var restoredPurchases: [SKPaymentTransaction] = []
    private var restoreCompletion: RestoreCompletion?
    
    override init() {
        super.init()
        SKPaymentQueue.default().add(self)
    }
    
    deinit {
        SKPaymentQueue.default().remove(self)
    }
    
    // MARK: - Public methods
    
    public func buy(_ purchase: Purchase, completion: @escaping (PurchaseResult) -> Void) {
        let payment = SKMutablePayment(product: purchase.item)
        payment.quantity = purchase.amount
        
        SKPaymentQueue.default().add(payment)
        
        activePurchases[purchase.item.productIdentifier] = (purchase, completion)
    }
    
    public func restore(completion: @escaping RestoreCompletion) {
        restoreCompletion = completion
        SKPaymentQueue.default().restoreCompletedTransactions()
    }
    
    // MARK: - Private methods
    
    private func purchased(transaction: SKPaymentTransaction) {
        let identifier = transaction.payment.productIdentifier
        guard let (purchase, completion) = activePurchases[identifier] else {
            return
        }
        activePurchases.removeValue(forKey: identifier)
        
        let result = PurchaseResult.succeeded(purchase, transaction)
        completion(result)
        
        if purchase.atomic == true {
            SKPaymentQueue.default().finishTransaction(transaction)
        }
    }
    
    private func failed(transaction: SKPaymentTransaction) {
        let identifier = transaction.payment.productIdentifier
        guard let (_, completion) = activePurchases[identifier] else {
            return
        }
        activePurchases.removeValue(forKey: identifier)
        
        guard let error = transaction.error else {
            return
        }
        
        let result = PurchaseResult.failed(error)
        completion(result)
    }
    
    private func restored(transaction: SKPaymentTransaction) {
        restoredPurchases.append(transaction)
    }
    
    // MARK: - SKPaymentTransactionObserver methods
    
    func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
        for transaction in transactions {
            switch transaction.transactionState {
            case .purchasing:
                break
            case .purchased:
                purchased(transaction: transaction)
            case .failed:
                failed(transaction: transaction)
            case .restored:
                restored(transaction: transaction)
            case .deferred:
                break
            }
        }
    }
    
    func paymentQueue(_ queue: SKPaymentQueue, restoreCompletedTransactionsFailedWithError error: Error) {
        restoreCompletion?(.failed(error))
        restoreCompletion = nil
    }
    
    func paymentQueueRestoreCompletedTransactionsFinished(_ queue: SKPaymentQueue) {
        restoreCompletion?(.succeeded(restoredPurchases))
        restoredPurchases.removeAll()
        restoreCompletion = nil
    }
}
