//
//  Purchase.swift
//  IAP
//
//  Created by Roman Karpievich on 4/11/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation
import StoreKit

open class Purchase {
    let item: SKProduct
    let finishing: Bool
    let amount: Int
    
    init(item: SKProduct,
         amount: Int = 1,
         finishing: Bool = true) {
        self.item = item
        self.amount = amount
        self.finishing = finishing
    }
}
