import { NativeModules } from "react-native";

// eslint-disable-next-line prefer-promise-reject-errors
const nullPromise = () => Promise.reject("ApplicasterIAP bridge is null");
const defaultIAP = {
  signUp: nullPromise,
};

const { ApplicasterIAPBridge = defaultIAP } = NativeModules;

export const ApplicasterIAPModule = {

  /**
   * Initialize bridge with given billing provider
   * @param {String} vendor: one of 'play' of 'amazon'
   */
  async init(vendor) {
    try {
      return ApplicasterIAPBridge.init(vendor);
    } catch (e) {
      throw e;
    }
  },

  /**
   * Retrieve product for payload
   * @param {Array} payload Array of products data
   */
  async products(payload) {
    try {
      return ApplicasterIAPBridge.products(payload);
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
      return ApplicasterIAPBridge.purchase(payload);
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
      return ApplicasterIAPBridge.finishPurchasedTransaction(payload);
    } catch (e) {
      throw e;
    }
  },
};
