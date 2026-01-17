package org.unifize.discountplatform.domain.payment;

import org.unifize.discountplatform.domain.PaymentMode;

/**
 * Payment method for credit/debit cards.
 */
public final class CardPaymentMethod implements PaymentMethod {
    private final PaymentMode mode;
    private final String bank;
    private final String cardType;

    private CardPaymentMethod(Builder builder) {
        this.mode = builder.mode;
        this.bank = builder.bank;
        this.cardType = builder.cardType;
    }

    @Override
    public PaymentMode getMode() { return mode; }

    public String getBank() { return bank; }

    public String getCardType() { return cardType; }

    @Override
    public boolean matches(PaymentMethodCriteria criteria) {
        if (criteria.getRequiredMode() != null && criteria.getRequiredMode() != mode) {
            return false;
        }
        if (criteria.getRequiredBank() != null &&
            !criteria.getRequiredBank().equalsIgnoreCase(bank)) {
            return false;
        }
        if (criteria.getRequiredCardType() != null &&
            !criteria.getRequiredCardType().equalsIgnoreCase(cardType)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bank + " " + cardType + " (" + mode + ")";
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private PaymentMode mode = PaymentMode.CREDIT_CARD;
        private String bank;
        private String cardType;

        public Builder mode(PaymentMode mode) { this.mode = mode; return this; }
        public Builder bank(String bank) { this.bank = bank; return this; }
        public Builder cardType(String cardType) { this.cardType = cardType; return this; }

        public CardPaymentMethod build() { return new CardPaymentMethod(this); }
    }
}
