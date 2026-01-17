package org.unifize.discountplatform.domain;

/**
 * Types of discounts supported by the platform.
 */
public enum DiscountType {
    BRAND(1000),      // Brand-specific discount (e.g., 40% off PUMA)
    CATEGORY(2000),   // Category-specific discount (e.g., 10% off T-shirts)
    VOUCHER(3000),    // Promotional voucher codes
    PAYMENT(4000);    // Payment method offers (e.g., ICICI card discount)

    private final int priority;

    DiscountType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
