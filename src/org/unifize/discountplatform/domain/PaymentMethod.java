package org.unifize.discountplatform.domain;

/**
 * Represents the payment method selected by the customer.
 */
public final class PaymentMethod {
    private final String bank;
    private final String cardType;

    public PaymentMethod(String bank, String cardType) {
        this.bank = bank;
        this.cardType = cardType;
    }

    public String getBank() {
        return bank;
    }

    public String getCardType() {
        return cardType;
    }

    public boolean matches(String requiredBank, String requiredCardType) {
        boolean bankMatch = requiredBank == null || requiredBank.equalsIgnoreCase(bank);
        boolean cardMatch = requiredCardType == null || requiredCardType.equalsIgnoreCase(cardType);
        return bankMatch && cardMatch;
    }

    @Override
    public String toString() {
        return bank + " " + cardType;
    }
}
