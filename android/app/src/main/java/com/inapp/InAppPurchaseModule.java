package com.inapp;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InAppPurchaseModule extends ReactContextBaseJavaModule {
    private ArrayList<String> paymentResponses = new ArrayList<>();
    private String OrderId;

    public InAppPurchaseModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "InAppPurchaseModule";
    }

    @ReactMethod
    public void initiatePurchase(String sku) {
        BillingClient billingClient = BillingClient.newBuilder(getReactApplicationContext())
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                        // Handle the purchase updates and update the payment response accordingly
                        boolean paymentSuccessful = false;
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    // Check if the orderId and packageName are valid
                                    if (purchase.getOrderId() != null) {
                                        // Purchase successful and the orderId and packageName are valid
                                        paymentSuccessful = true;
                                        OrderId = purchase.getOrderId();
                                        break;
                                    }
                                }
                            }
                        } else {
                            // Purchase failed
                            paymentSuccessful = false;
                        }

                        // Store the payment response in the array
                        if (paymentSuccessful == true) {
                            paymentResponses.add(paymentSuccessful ? "Payment successful" : "Payment failed");
                            paymentResponses.add(String.valueOf(OrderId));
                        } else {
                            paymentResponses.add(paymentSuccessful ? "Payment successful" : "Payment failed");
                        }
//                          paymentResponses.add(paymentSuccessful ? "Payment successful" : "Payment failed");

                    }
                })
                .build();

        // Start the billing client connection
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Perform the purchase flow
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                            .setSkusList(Collections.singletonList(sku))
                            .setType(BillingClient.SkuType.INAPP) // or BillingClient.SkuType.SUBS for subscriptions
                            .build();

                    billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                                if (!skuDetailsList.isEmpty()) {
                                    SkuDetails skuDetails = skuDetailsList.get(0);
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build();
                                    billingClient.launchBillingFlow(getCurrentActivity(), flowParams);
                                }
                            } else {
                                // Handle sku details query failure
                            }
                        }
                    });
                } else {
                    // Handle billing client setup failure
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Handle billing service disconnection
            }
        });
    }


    @ReactMethod
    public void getPaymentResponses(Promise promise) {
        WritableArray responses = new WritableNativeArray();
        for (String response : paymentResponses) {
            responses.pushString(response);
        }
        promise.resolve(responses);
    }
}
