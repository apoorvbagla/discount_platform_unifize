package org.unifize.discountplatform.domain.payment;

import org.unifize.discountplatform.domain.PaymentMode;

/**
 * Criteria for matching payment methods in discount rules.
 */
public final class PaymentMethodCriteria {
    private final PaymentMode requiredMode;
    private final String requiredBank;
    private final String requiredCardType;
    private final String requiredWalletProvider;
    private final String requiredUpiApp;

    private PaymentMethodCriteria(Builder builder) {
        this.requiredMode = builder.requiredMode;
        this.requiredBank = builder.requiredBank;
        this.requiredCardType = builder.requiredCardType;
        this.requiredWalletProvider = builder.requiredWalletProvider;
        this.requiredUpiApp = builder.requiredUpiApp;
    }

    public PaymentMode getRequiredMode() { return requiredMode; }
    public String getRequiredBank() { return requiredBank; }
    public String getRequiredCardType() { return requiredCardType; }
    public String getRequiredWalletProvider() { return requiredWalletProvider; }
    public String getRequiredUpiApp() { return requiredUpiApp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private PaymentMode requiredMode;
        private String requiredBank;
        private String requiredCardType;
        private String requiredWalletProvider;
        private String requiredUpiApp;

        public Builder requiredMode(PaymentMode requiredMode) {
            this.requiredMode = requiredMode;
            return this;
        }

        public Builder requiredBank(String requiredBank) {
            this.requiredBank = requiredBank;
            return this;
        }

        public Builder requiredCardType(String requiredCardType) {
            this.requiredCardType = requiredCardType;
            return this;
        }

        public Builder requiredWalletProvider(String requiredWalletProvider) {
            this.requiredWalletProvider = requiredWalletProvider;
            return this;
        }

        public Builder requiredUpiApp(String requiredUpiApp) {
            this.requiredUpiApp = requiredUpiApp;
            return this;
        }

        public PaymentMethodCriteria build() {
            return new PaymentMethodCriteria(this);
        }
    }
}
