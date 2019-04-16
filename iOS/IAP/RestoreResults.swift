//
//  RestoreResults.swift
//  IAP
//
//  Created by Roman Karpievich on 4/12/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation
import StoreKit

enum RestoreResult {
    case succeeded([SKPaymentTransaction])
    case failed(Error)
}
