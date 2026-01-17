package org.unifize.discountplatform.engine;

import org.unifize.discountplatform.domain.*;
import org.unifize.discountplatform.domain.strategy.DiscountStrategy;

import java.util.*;

/**
 * Core discount calculation engine.
 * Applies discounts in priority order using the Strategy pattern.
 */
public final class DiscountCalculator {

    /**
     * Calculate final price after applying all eligible discounts.
     * Uses Strategy pattern - each DiscountStrategy handles its own application logic.
     *
     * @param cart       The shopping cart with items
     * @param strategies Available discount strategies
     * @return Result containing final price, applied discounts, and reasoning
     */
    public DiscountResult calculateDiscounts(Cart cart, List<DiscountStrategy> strategies) {
        if (cart.isEmpty()) {
            return DiscountResult.builder()
                    .originalTotal(Money.zero())
                    .finalPrice(Money.zero())
                    .appendReasoning("Cart is empty, no discounts applied.")
                    .build();
        }

        Money originalTotal = cart.getOriginalTotal();
        DiscountResult.Builder resultBuilder = DiscountResult.builder()
                .originalTotal(originalTotal);

        // Track current price per item (after discounts applied so far)
        Map<CartItem, Money> itemPrices = new HashMap<>();
        for (CartItem item : cart.getItems()) {
            itemPrices.put(item, item.getTotalPrice());
        }

        // Sort strategies by priority
        List<DiscountStrategy> sortedStrategies = new ArrayList<>(strategies);
        sortedStrategies.sort(Comparator.comparingInt(DiscountStrategy::getPriority));

        resultBuilder.appendReasoning("Starting calculation with cart total: " + originalTotal);

        // Apply each strategy in order (no more switch statement!)
        for (DiscountStrategy strategy : sortedStrategies) {
            Money discountAmount = strategy.apply(cart, itemPrices, resultBuilder);

            if (discountAmount.isGreaterThan(Money.zero())) {
                AppliedDiscount applied = new AppliedDiscount(
                        strategy.getId(),
                        strategy.getType(),
                        discountAmount,
                        strategy.getDescription()
                );
                resultBuilder.addAppliedDiscount(applied);
            }
        }

        // Calculate final price
        Money finalPrice = itemPrices.values().stream()
                .reduce(Money.zero(), Money::add);

        resultBuilder.finalPrice(finalPrice);
        resultBuilder.appendReasoning("Final price after all discounts: " + finalPrice);

        return resultBuilder.build();
    }
}
