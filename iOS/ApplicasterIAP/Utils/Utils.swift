//
//  Utils.swift
//  ApplicasterIAP
//
//  Created by Anton Kononenko on 4/13/20.
//  Copyright © 2020 Roman Karpievich. All rights reserved.
//

import Foundation

class Utils {
    class func retrieveReceipt(completion: (_ receipt: String?) -> Void) {
        if let appStoreReceiptURL = Bundle.main.appStoreReceiptURL,
            FileManager.default.fileExists(atPath: appStoreReceiptURL.path) {
            do {
                let receiptData = try Data(contentsOf: appStoreReceiptURL, options: .alwaysMapped)
                let receiptString = receiptData.base64EncodedString(options: [])
                completion(receiptString)
            } catch {
                debugPrint("Couldn't read receipt data with error: " + error.localizedDescription)
                completion(nil)
            }
        }
    }
}
