@import React;

@interface RCT_EXTERN_MODULE (ApplicasterIAPBridge, NSObject)
RCT_EXTERN_METHOD(products:(NSSet *)identifiers
                      resolver:(RCTPromiseResolveBlock)resolver
                          rejecter:(RCTPromiseRejectBlock)rejecter);

RCT_EXTERN_METHOD(purchase:(NSString *)productIdentifier
                      resolver:(RCTPromiseResolveBlock)resolver
                          rejecter:(RCTPromiseRejectBlock)rejecter);

RCT_EXTERN_METHOD(restore:(RCTPromiseResolveBlock)resolver
                      rejecter:(RCTPromiseRejectBlock)rejecter);
@end
