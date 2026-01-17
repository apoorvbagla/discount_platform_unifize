package org.unifize.discountplatform.engine;

import org.unifize.discountplatform.domain.*;

import java.util.*;

/**
 * Core discount calculation engine.
 * Applies discounts in priority order: Brand -> Category -> Voucher -> Payment
 */
public final class DiscountCalculator {

    /**
     * Calculate final price after applying all eligible discounts.
     *
     * @param cart      The shopping cart with items
     * @param discounts Available discount rules
     * @return Result containing final price, applied discounts, and reasoning
     */
    public DiscountResult calculateDiscounts(Cart cart, List<Discount> discounts) {
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

        // Sort discounts by priority
        List<Discount> sortedDiscounts = new ArrayList<>(discounts);
        sortedDiscounts.sort(Comparator.comparingInt(Discount::getPriority));

        resultBuilder.appendReasoning("Starting calculation with cart total: " + originalTotal);

        // Apply each discount in order
        for (Discount discount : sortedDiscounts) {
            Money discountAmount = applyDiscount(cart, discount, itemPrices, resultBuilder);

            if (discountAmount.isGreaterThan(Money.zero())) {
                AppliedDiscount applied = new AppliedDiscount(
                        discount.getId(),
                        discount.getType(),
                        discountAmount,
                        discount.getDescription()
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

    private Money applyDiscount(Cart cart, Discount discount,
                                Map<CartItem, Money> itemPrices,
                                DiscountResult.Builder resultBuilder) {
        switch (discount.getType()) {
            case BRAND:
                return applyBrandDiscount(cart, discount, itemPrices, resultBuilder);
            case CATEGORY:
                return applyCategoryDiscount(cart, discount, itemPrices, resultBuilder);
            case VOUCHER:
                return applyVoucherDiscount(cart, discount, itemPrices, resultBuilder);
            case PAYMENT:
                return applyPaymentDiscount(cart, discount, itemPrices, resultBuilder);
            default:
                resultBuilder.addSkippedReason(discount.getId() + ": Unknown discount type");
                return Money.zero();
        }
    }

    private Money applyBrandDiscount(Cart cart, Discount discount,
                                     Map<CartItem, Money> itemPrices,
                                     DiscountResult.Builder resultBuilder) {
        Money totalDiscount = Money.zero();
        String targetBrand = discount.getTargetBrand();

        for (CartItem item : cart.getItems()) {
            if (item.getBrand().equalsIgnoreCase(targetBrand)) {
                Money currentPrice = itemPrices.get(item);
                Money itemDiscount = currentPrice.percentage(discount.getDiscountPercent());

                // Apply discount
                Money newPrice = currentPrice.subtract(itemDiscount);
                itemPrices.put(item, newPrice);
                totalDiscount = totalDiscount.add(itemDiscount);

                resultBuilder.appendReasoning(String.format(
                        "  %s: %s -> %s (%d%% off %s)",
                        discount.getId(), currentPrice, newPrice,
                        discount.getDiscountPercent(), targetBrand));
            }
        }

        if (totalDiscount.equals(Money.zero())) {
            resultBuilder.addSkippedReason(discount.getId() + ": No " + targetBrand + " items in cart");
        }

        return totalDiscount;
    }

    private Money applyCategoryDiscount(Cart cart, Discount discount,
                                        Map<CartItem, Money> itemPrices,
                                        DiscountResult.Builder resultBuilder) {
        Money totalDiscount = Money.zero();
        String targetCategory = discount.getTargetCategory();

        for (CartItem item : cart.getItems()) {
            if (item.getCategory().equalsIgnoreCase(targetCategory)) {
                Money currentPrice = itemPrices.get(item);
                Money itemDiscount = currentPrice.percentage(discount.getDiscountPercent());

                Money newPrice = currentPrice.subtract(itemDiscount);
                itemPrices.put(item, newPrice);
                totalDiscount = totalDiscount.add(itemDiscount);

                resultBuilder.appendReasoning(String.format(
                        "  %s: %s -> %s (%d%% off %s)",
                        discount.getId(), currentPrice, newPrice,
                        discount.getDiscountPercent(), targetCategory));
            }
        }

        if (totalDiscount.equals(Money.zero())) {
            resultBuilder.addSkippedReason(discount.getId() + ": No " + targetCategory + " items in cart");
        }

        return totalDiscount;
    }

    private Money applyVoucherDiscount(Cart cart, Discount discount,
                                       Map<CartItem, Money> itemPrices,
                                       DiscountResult.Builder resultBuilder) {
        Money totalDiscount = Money.zero();
        Money maxCap = discount.getMaxDiscountCap();

        for (CartItem item : cart.getItems()) {
            // Check brand exclusions
            if (discount.isBrandExcluded(item.getBrand())) {
                resultBuilder.appendReasoning(String.format(
                        "  %s: Skipping %s (brand %s excluded)",
                        discount.getId(), item.getName(), item.getBrand()));
                continue;
            }

            Money currentPrice = itemPrices.get(item);
            Money itemDiscount = currentPrice.percentage(discount.getDiscountPercent());

            // Check if we'd exceed the cap
            if (maxCap != null) {
                Money remaining = maxCap.subtract(totalDiscount);
                if (remaining.isLessThan(itemDiscount) || remaining.equals(Money.zero())) {
                    if (remaining.isGreaterThan(Money.zero())) {
                        itemDiscount = remaining;
                    } else {
                        resultBuilder.appendReasoning(String.format(
                                "  %s: Cap reached, skipping %s",
                                discount.getId(), item.getName()));
                        continue;
                    }
                }
            }

            Money newPrice = currentPrice.subtract(itemDiscount);
            itemPrices.put(item, newPrice);
            totalDiscount = totalDiscount.add(itemDiscount);

            resultBuilder.appendReasoning(String.format(
                    "  %s: %s -> %s (%d%% voucher)",
                    discount.getId(), currentPrice, newPrice, discount.getDiscountPercent()));
        }

        if (maxCap != null && totalDiscount.isGreaterThan(Money.zero())) {
            resultBuilder.appendReasoning(String.format(
                    "  %s: Total voucher discount: %s (cap: %s)",
                    discount.getId(), totalDiscount, maxCap));
        }

        return totalDiscount;
    }

    private Money applyPaymentDiscount(Cart cart, Discount discount,
                                       Map<CartItem, Money> itemPrices,
                                       DiscountResult.Builder resultBuilder) {
        PaymentMethod payment = cart.getPaymentMethod();

        // Check payment method eligibility
        if (payment == null) {
            resultBuilder.addSkippedReason(discount.getId() + ": No payment method specified");
            return Money.zero();
        }

        if (!payment.matches(discount.getRequiredBank(), discount.getRequiredCardType())) {
            resultBuilder.addSkippedReason(String.format(
                    "%s: Payment method %s doesn't match required %s %s",
                    discount.getId(), payment, discount.getRequiredBank(), discount.getRequiredCardType()));
            return Money.zero();
        }

        // Calculate current cart total
        Money currentTotal = itemPrices.values().stream()
                .reduce(Money.zero(), Money::add);

        // Check minimum cart value
        if (discount.getMinCartValue() != null && currentTotal.isLessThan(discount.getMinCartValue())) {
            resultBuilder.addSkippedReason(String.format(
                    "%s: Cart total %s below minimum %s",
                    discount.getId(), currentTotal, discount.getMinCartValue()));
            return Money.zero();
        }

        // Calculate discount on total
        Money paymentDiscount = currentTotal.percentage(discount.getDiscountPercent());

        // Apply cap
        if (discount.getMaxDiscountCap() != null) {
            paymentDiscount = paymentDiscount.min(discount.getMaxDiscountCap());
        }

        // Distribute discount proportionally across items
        if (paymentDiscount.isGreaterThan(Money.zero())) {
            distributeDiscountProportionally(itemPrices, currentTotal, paymentDiscount);
            resultBuilder.appendReasoning(String.format(
                    "  %s: %d%% payment discount = %s (capped at %s)",
                    discount.getId(), discount.getDiscountPercent(),
                    paymentDiscount, discount.getMaxDiscountCap()));
        }

        return paymentDiscount;
    }

    private void distributeDiscountProportionally(Map<CartItem, Money> itemPrices,
                                                  Money currentTotal,
                                                  Money discountAmount) {
        Money remaining = discountAmount;
        List<CartItem> items = new ArrayList<>(itemPrices.keySet());

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Money itemPrice = itemPrices.get(item);

            Money itemShare;
            if (i == items.size() - 1) {
                // Last item gets remaining to avoid rounding issues
                itemShare = remaining;
            } else {
                // Proportional share
                double ratio = (double) itemPrice.getPaise() / currentTotal.getPaise();
                itemShare = Money.ofPaise(Math.round(discountAmount.getPaise() * ratio));
            }

            itemShare = itemShare.min(itemPrice); // Cannot exceed item price
            itemPrices.put(item, itemPrice.subtract(itemShare));
            remaining = remaining.subtract(itemShare);
        }
    }
}
