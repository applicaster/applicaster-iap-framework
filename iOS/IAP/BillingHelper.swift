//
//  BillingHelper.swift
//  IAP
//
//  Created by Roman Karpievich on 4/9/19.
//  Copyright © 2019 Roman Karpievich. All rights reserved.
//

import Foundation

class BillingHelper {
    
    public static let sharedInstance = BillingHelper()
    
    private var productsQuery: ProductsQuery?
    
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
    
    // MARK: - Private methods

    
}
