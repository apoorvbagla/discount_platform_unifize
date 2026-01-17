package org.unifize.discountplatform.domain.strategy;

import org.unifize.discountplatform.domain.*;
import java.util.*;

/**
 * Strategy for voucher/promotional code discounts.
 * Supports brand exclusions and discount caps.
 */
public final class VoucherDiscount extends AbstractDiscountStrategy {

    private final String voucherCode;
    private final Set<String> excludedBrands;
    private final String minCustomerTier;

    private VoucherDiscount(Builder builder) {
        super(builder);
        this.voucherCode = builder.voucherCode;
        this.excludedBrands = builder.excludedBrands != null
                ? Collections.unmodifiableSet(new HashSet<>(builder.excludedBrands))
                : Collections.emptySet();
        this.minCustomerTier = builder.minCustomerTier;
    }

    public String getVoucherCode() { return voucherCode; }
    public Set<String> getExcludedBrands() { return excludedBrands; }
    public String getMinCustomerTier() { return minCustomerTier; }

    public boolean isBrandExcluded(String brand) {
        return excludedBrands.stream()
                .anyMatch(excluded -> excluded.equalsIgnoreCase(brand));
    }

    @Override
    public Money apply(Cart cart, Map<CartItem, Money> itemPrices,
                       DiscountResult.Builder resultBuilder) {
        Money totalDiscount = Money.zero();

        for (CartItem item : cart.getItems()) {
            // Check brand exclusions
            if (isBrandExcluded(item.getBrand())) {
                resultBuilder.appendReasoning(String.format(
                        "  %s: Skipping %s (brand %s excluded)",
                        id, item.getName(), item.getBrand()));
                continue;
            }

            Money currentPrice = itemPrices.get(item);
            Money itemDiscount = currentPrice.percentage(discountPercent);

            // Check if we'd exceed the cap
            if (maxDiscountCap != null) {
                Money remaining = maxDiscountCap.subtract(totalDiscount);
                if (remaining.isLessThan(itemDiscount) || remaining.equals(Money.zero())) {
                    if (remaining.isGreaterThan(Money.zero())) {
                        itemDiscount = remaining;
                    } else {
                        resultBuilder.appendReasoning(String.format(
                                "  %s: Cap reached, skipping %s",
                                id, item.getName()));
                        continue;
                    }
                }
            }

            Money newPrice = currentPrice.subtract(itemDiscount);
            itemPrices.put(item, newPrice);
            totalDiscount = totalDiscount.add(itemDiscount);

            resultBuilder.appendReasoning(String.format(
                    "  %s: %s -> %s (%d%% voucher %s)",
                    id, currentPrice, newPrice, discountPercent, voucherCode));
        }

        if (maxDiscountCap != null && totalDiscount.isGreaterThan(Money.zero())) {
            resultBuilder.appendReasoning(String.format(
                    "  %s: Total voucher discount: %s (cap: %s)",
                    id, totalDiscount, maxDiscountCap));
        }

        return totalDiscount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder extends AbstractBuilder<Builder> {
        private String voucherCode;
        private Set<String> excludedBrands;
        private String minCustomerTier;

        @Override
        protected Builder self() { return this; }

        public Builder voucherCode(String voucherCode) {
            this.voucherCode = voucherCode;
            return this;
        }

        public Builder excludedBrands(Set<String> excludedBrands) {
            this.excludedBrands = excludedBrands;
            return this;
        }

        public Builder minCustomerTier(String minCustomerTier) {
            this.minCustomerTier = minCustomerTier;
            return this;
        }

        public VoucherDiscount build() {
            this.type = DiscountType.VOUCHER;
            return new VoucherDiscount(this);
        }
    }
}
