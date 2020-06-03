import { NativeModules } from "react-native";

// eslint-disable-next-line prefer-promise-reject-errors
const nullPromise = () => Promise.reject("ApplicasterIAP bridge is null");
const defaultIAP = {
  signUp: nullPromise,
};

const { ApplicasterIAPBridge = defaultIAP } = NativeModules;

export const ApplicasterIAPModule = {
  /**
   * Retrieve product fro identifiers
   * @param {Array} identifiers Dictionary with user data
   */
  async products(identifiers) {
    try {
      return ApplicasterIAPBridge.products(identifiers);
    } catch (e) {
      throw e;
    }
  },
  /**
   * Purchase item
   * @param {String} productIdentifier Dictionary with user data
   * @param {Boolean} finishTransactionAfterPurchase Defines if native side should finish transaction
   */
  async purchase(productIdentifier, finishTransactionAfterPurchase) {
    try {
      return ApplicasterIAPBridge.purchase(
        productIdentifier,
        finishTransactionAfterPurchase
      );
    } catch (e) {
      throw e;
    }
  },
  /**
   * Restore Purchases
   */
  async restore() {
    try {
      return ApplicasterIAPBridge.restore();
    } catch (e) {
      throw e;
    }
  },
  /**
   * Finish purchased transaction
   */
  async finishPurchasedTransaction(transactionIdentifier) {
    try {
      return ApplicasterIAPBridge.finishPurchasedTransaction(
        transactionIdentifier
      );
    } catch (e) {
      throw e;
    }
  },
};
