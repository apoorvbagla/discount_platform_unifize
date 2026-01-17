package org.unifize.discountplatform.domain.payment;

import org.unifize.discountplatform.domain.PaymentMode;

/**
 * Payment method for UPI payments.
 */
public final class UpiPaymentMethod implements PaymentMethod {
    private final String upiId;
    private final String app;

    private UpiPaymentMethod(Builder builder) {
        this.upiId = builder.upiId;
        this.app = builder.app;
    }

    @Override
    public PaymentMode getMode() { return PaymentMode.UPI; }

    public String getUpiId() { return upiId; }

    public String getApp() { return app; }

    @Override
    public boolean matches(PaymentMethodCriteria criteria) {
        if (criteria.getRequiredMode() != null && criteria.getRequiredMode() != PaymentMode.UPI) {
            return false;
        }
        if (criteria.getRequiredUpiApp() != null &&
            !criteria.getRequiredUpiApp().equalsIgnoreCase(app)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return app + " UPI (" + upiId + ")";
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String upiId;
        private String app;

        public Builder upiId(String upiId) { this.upiId = upiId; return this; }
        public Builder app(String app) { this.app = app; return this; }

        public UpiPaymentMethod build() { return new UpiPaymentMethod(this); }
    }
}
