package org.unifize.discountplatform.domain;

/**
 * Types of discounts supported by the platform.
 */
public enum DiscountType {
    BRAND(1),      // Brand-specific discount (e.g., 40% off PUMA)
    CATEGORY(2),   // Category-specific discount (e.g., 10% off T-shirts)
    VOUCHER(3),    // Promotional voucher codes
    PAYMENT(4);    // Payment method offers (e.g., ICICI card discount)

    private final int priority;

    DiscountType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
