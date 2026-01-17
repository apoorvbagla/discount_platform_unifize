package org.unifize.discountplatform.domain.strategy;

import org.unifize.discountplatform.domain.*;
import java.util.Map;

/**
 * Strategy for brand-specific discounts.
 * Applies discount to all items of a specific brand.
 */
public final class BrandDiscount extends AbstractDiscountStrategy {

    private final String targetBrand;

    private BrandDiscount(Builder builder) {
        super(builder);
        this.targetBrand = builder.targetBrand;
    }

    public String getTargetBrand() { return targetBrand; }

    @Override
    public Money apply(Cart cart, Map<CartItem, Money> itemPrices,
                       DiscountResult.Builder resultBuilder) {
        Money totalDiscount = Money.zero();

        for (CartItem item : cart.getItems()) {
            if (item.getBrand().equalsIgnoreCase(targetBrand)) {
                Money currentPrice = itemPrices.get(item);
                Money itemDiscount = currentPrice.percentage(discountPercent);

                Money newPrice = currentPrice.subtract(itemDiscount);
                itemPrices.put(item, newPrice);
                totalDiscount = totalDiscount.add(itemDiscount);

                resultBuilder.appendReasoning(String.format(
                        "  %s: %s -> %s (%d%% off %s)",
                        id, currentPrice, newPrice, discountPercent, targetBrand));
            }
        }

        if (totalDiscount.equals(Money.zero())) {
            resultBuilder.addSkippedReason(id + ": No " + targetBrand + " items in cart");
        }

        return totalDiscount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder extends AbstractBuilder<Builder> {
        private String targetBrand;

        @Override
        protected Builder self() { return this; }

        public Builder targetBrand(String targetBrand) {
            this.targetBrand = targetBrand;
            return this;
        }

        public BrandDiscount build() {
            this.type = DiscountType.BRAND;
            return new BrandDiscount(this);
        }
    }
}
