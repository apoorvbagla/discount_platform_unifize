package org.unifize.discountplatform.domain.payment;

import org.unifize.discountplatform.domain.PaymentMode;

/**
 * Factory for creating payment method instances based on payment mode.
 */
public final class PaymentMethodFactory {

    private PaymentMethodFactory() {} // Prevent instantiation

    /**
     * Create a credit card payment method.
     */
    public static CardPaymentMethod createCreditCard(String bank, String cardType) {
        return CardPaymentMethod.builder()
                .mode(PaymentMode.CREDIT_CARD)
                .bank(bank)
                .cardType(cardType)
                .build();
    }

    /**
     * Create a debit card payment method.
     */
    public static CardPaymentMethod createDebitCard(String bank, String cardType) {
        return CardPaymentMethod.builder()
                .mode(PaymentMode.DEBIT_CARD)
                .bank(bank)
                .cardType(cardType)
                .build();
    }

    /**
     * Create a UPI payment method.
     */
    public static UpiPaymentMethod createUpi(String upiId, String app) {
        return UpiPaymentMethod.builder()
                .upiId(upiId)
                .app(app)
                .build();
    }

    /**
     * Create a wallet payment method.
     */
    public static WalletPaymentMethod createWallet(String provider) {
        return WalletPaymentMethod.builder()
                .provider(provider)
                .build();
    }
}
