package org.unifize.discountplatform.domain.payment;

import org.unifize.discountplatform.domain.PaymentMode;

/**
 * Payment method for digital wallets.
 */
public final class WalletPaymentMethod implements PaymentMethod {
    private final String provider;

    private WalletPaymentMethod(Builder builder) {
        this.provider = builder.provider;
    }

    @Override
    public PaymentMode getMode() { return PaymentMode.WALLET; }

    public String getProvider() { return provider; }

    @Override
    public boolean matches(PaymentMethodCriteria criteria) {
        if (criteria.getRequiredMode() != null && criteria.getRequiredMode() != PaymentMode.WALLET) {
            return false;
        }
        if (criteria.getRequiredWalletProvider() != null &&
            !criteria.getRequiredWalletProvider().equalsIgnoreCase(provider)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return provider + " Wallet";
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String provider;

        public Builder provider(String provider) { this.provider = provider; return this; }

        public WalletPaymentMethod build() { return new WalletPaymentMethod(this); }
    }
}
