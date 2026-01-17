package org.unifize.discountplatform.domain;

/**
 * Records a discount that was applied during calculation.
 */
public final class AppliedDiscount {
    private final String discountId;
    private final DiscountType type;
    private final Money amount;
    private final String description;

    public AppliedDiscount(String discountId, DiscountType type, Money amount, String description) {
        this.discountId = discountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public String getDiscountId() {
        return discountId;
    }

    public DiscountType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s: -%s (%s)", discountId, amount, description);
    }
}
