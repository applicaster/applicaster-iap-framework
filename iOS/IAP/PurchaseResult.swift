//
//  PurchaseResult.swift
//  IAP
//
//  Created by Roman Karpievich on 4/11/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation
import StoreKit

enum PurchaseResult {
    case succeeded(Purchase, SKPaymentTransaction)
    case failed(Error)
}
