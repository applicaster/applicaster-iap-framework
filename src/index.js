import { NativeModules } from "react-native";

// eslint-disable-next-line prefer-promise-reject-errors
const nullPromise = () => Promise.reject("ApplicasterIAP bridge is null");
const defaultIAP = {
  signUp: nullPromise,
};

const { ApplicasterIAPBridge = defaultIAP } = NativeModules;

export const ApplicasterIAPModule = {
  /**
   * Retrieve product for payload
   * @param {Array} payload Array of products data
   */
  async products(payload) {
    try {
      return ApplicasterIAPBridge.products(identifiers);
    } catch (e) {
      throw e;
    }
  },

  /**
   * Purchase item
   * @param {Object} payload Dictionary with user data
   * @param {Boolean} finishTransactionAfterPurchase Defines if native side should finish transaction
   */
  async purchase(payload, finishTransactionAfterPurchase) {
    try {
      return ApplicasterIAPBridge.purchase(
        payload,
        finishTransactionAfterPurchase
      );
    } catch (e) {
      throw e;
    }
  },

  /**
   * Purchase item
   * @param {Object} payload Dictionary with user data
   */
  async purchase(payload) {
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
   * @param {Object} payload Dictionary transaction to finalize
   */
  async finishPurchasedTransaction(payload) {
    try {
      return ApplicasterIAPBridge.finishPurchasedTransaction(
        transactionIdentifier
      );
    } catch (e) {
      throw e;
    }
  },
};
