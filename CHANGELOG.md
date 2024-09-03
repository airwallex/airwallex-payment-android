# CHANGELOG

## 6.6.0 - 2024-08-29
* [REMOVED][135](https://github.com/airwallex/airwallex-payment-android/pull/135) Removed ClientSecretProvider from the com.airwallex.android.core.Airwallex.Companion#initialize method.
* [ADDED][143](https://github.com/airwallex/airwallex-payment-android/pull/143) Added payment by PaymentConsent with conditional CVC popup.
* [ADDED][135](https://github.com/airwallex/airwallex-payment-android/pull/135) Added payment from card info collector.
* [CHANGED][139](https://github.com/airwallex/airwallex-payment-android/pull/139) Refactored Sample app.

## 5.0.4 - 2024-08-22
* [CHANGED] Supported Amex, Discover, JCB in GooglePay

## 5.0.3 - 2024-08-13
* [ADDED] Added parameter to skip Google IsReadyToPay

## 5.0.2 - 2024-08-06
* [FIXED] Fixed the crash caused by coroutine scope when retrieving the payment list.

## 5.0.1 - 2024-07-26
* [ADDED][134](https://github.com/airwallex/airwallex-payment-android/pull/134) Added risk session ID to signifyd and device data in confirm intent request

## 5.0.0 - 2024-07-12
* [REMOVED][123](https://github.com/airwallex/airwallex-payment-android/pull/123) Removed the handlePaymentData method from com.airwallex.android.AirwallexStarter.
* [REMOVED][123](https://github.com/airwallex/airwallex-payment-android/pull/123) Removed the handlePaymentData method from com.airwallex.android.core.Airwallex.
* [ADDED][123](https://github.com/airwallex/airwallex-payment-android/pull/123) Added the parameter Application to the com.airwallex.android.core.Airwallex.Companion#initialize method.
