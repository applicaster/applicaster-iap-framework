//
//  ProductsQueryResult.swift
//  IAP
//
//  Created by Roman Karpievich on 4/9/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation
import StoreKit

struct ProductsQueryResult {
    public var invalidIdentifiers: [String]
    public var products: [SKProduct]
    public var error: Error?
}
