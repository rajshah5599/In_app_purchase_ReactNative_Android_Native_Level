import {NativeModules} from 'react-native';

const {InAppPurchaseModule} = NativeModules;

export const initiatePurchase = (sku: any) => {
  InAppPurchaseModule.initiatePurchase(sku);
};

export const getPaymentResponses = () => {
  return InAppPurchaseModule.getPaymentResponses();
};
