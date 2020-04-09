import Foundation
import React
import StoreKit

struct SKPaymentTransactionKeys {
    static let error = "error"
    static let transactionState = "transactionState"
}

extension SKPaymentTransactionState {
    func toString() -> String {
        switch self {
        case .purchasing:
            return "purchasing"
        case .purchased:
            return "purchased"
        case .failed:
            return "failed"
        case .restored:
            return "restored"
        case .deferred:
            return "deferred"
        default:
            return "unknown"
        }
    }
}

extension SKPaymentTransaction {
    class func wrappTransactionToDictionary(paymentTransaction: SKPaymentTransaction) -> [String: Any] {
        return [
            SKPaymentTransactionKeys.error: RCTMakeError("SKPaymentTransaction Error", paymentTransaction.error?.localizedDescription,
                                                         nil),
            SKPaymentTransactionKeys.transactionState: paymentTransaction.transactionState.toString(),
        ]
    }
}
