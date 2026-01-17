package org.unifize.discountplatform.domain.payment;

import org.unifize.discountplatform.domain.PaymentMode;

/**
 * Base interface for all payment methods.
 */
public interface PaymentMethod {
    /**
     * @return The payment mode (CREDIT_CARD, UPI, WALLET, etc.)
     */
    PaymentMode getMode();

    /**
     * Check if this payment method matches the given criteria.
     * @param criteria The criteria to match against
     * @return true if this payment method matches all specified criteria
     */
    boolean matches(PaymentMethodCriteria criteria);
}
