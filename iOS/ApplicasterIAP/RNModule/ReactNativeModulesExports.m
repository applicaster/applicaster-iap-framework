@import React;

@interface RCT_EXTERN_MODULE (ApplicasterIAPBridge, NSObject)

RCT_EXTERN_METHOD(products:(NSSet *)identifiers
                      resolver:(RCTPromiseResolveBlock)resolver
                          rejecter:(RCTPromiseRejectBlock)rejecter);

RCT_EXTERN_METHOD(purchase:(NSString *)productIdentifier
                      finishing:(nonnull NSNumber *)finishing
                          resolver:(RCTPromiseResolveBlock)resolver
                              rejecter:(RCTPromiseRejectBlock)rejecter);

RCT_EXTERN_METHOD(restore:(RCTPromiseResolveBlock)resolver
                      rejecter:(RCTPromiseRejectBlock)rejecter);

RCT_EXTERN_METHOD(finishPurchasedTransaction:(NSString *)transactionIdentifier
                      resolver:(RCTPromiseResolveBlock)resolver
                          rejecter:(RCTPromiseRejectBlock)rejecter);
@end
