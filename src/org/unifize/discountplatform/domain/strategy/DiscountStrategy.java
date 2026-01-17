package org.unifize.discountplatform.domain.strategy;

import org.unifize.discountplatform.domain.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Strategy interface for all discount types.
 * Each discount type implements its own eligibility and application logic.
 */
public interface DiscountStrategy {

    /**
     * @return Unique identifier for this discount
     */
    String getId();

    /**
     * @return Type of discount (BRAND, CATEGORY, VOUCHER, PAYMENT)
     */
    DiscountType getType();

    /**
     * @return Priority for applying this discount (lower = higher priority)
     */
    int getPriority();

    /**
     * @return Description of the discount
     */
    String getDescription();

    /**
     * @return Discount percentage (0-100)
     */
    int getDiscountPercent();

    /**
     * @return Maximum discount cap, or null if uncapped
     */
    Money getMaxDiscountCap();

    /**
     * @return When this discount was created
     */
    LocalDateTime getCreatedDateTime();

    /**
     * @return When this discount was last updated
     */
    LocalDateTime getLastUpdatedDateTime();

    /**
     * Calculate and apply the discount to applicable items.
     *
     * @param cart The shopping cart
     * @param itemPrices Current prices of items (mutable, updated in place)
     * @param resultBuilder Builder for recording reasoning
     * @return Total discount amount applied
     */
    Money apply(Cart cart, Map<CartItem, Money> itemPrices,
                DiscountResult.Builder resultBuilder);
}
