package org.unifize.discountplatform.domain.strategy;

import org.unifize.discountplatform.domain.*;
import java.util.Map;

/**
 * Strategy for category-specific discounts.
 * Applies discount to all items in a specific category.
 */
public final class CategoryDiscount extends AbstractDiscountStrategy {

    private final String targetCategory;

    private CategoryDiscount(Builder builder) {
        super(builder);
        this.targetCategory = builder.targetCategory;
    }

    public String getTargetCategory() { return targetCategory; }

    @Override
    public Money apply(Cart cart, Map<CartItem, Money> itemPrices,
                       DiscountResult.Builder resultBuilder) {
        Money totalDiscount = Money.zero();

        for (CartItem item : cart.getItems()) {
            if (item.getCategory().equalsIgnoreCase(targetCategory)) {
                Money currentPrice = itemPrices.get(item);
                Money itemDiscount = currentPrice.percentage(discountPercent);

                Money newPrice = currentPrice.subtract(itemDiscount);
                itemPrices.put(item, newPrice);
                totalDiscount = totalDiscount.add(itemDiscount);

                resultBuilder.appendReasoning(String.format(
                        "  %s: %s -> %s (%d%% off %s)",
                        id, currentPrice, newPrice, discountPercent, targetCategory));
            }
        }

        if (totalDiscount.equals(Money.zero())) {
            resultBuilder.addSkippedReason(id + ": No " + targetCategory + " items in cart");
        }

        return totalDiscount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder extends AbstractBuilder<Builder> {
        private String targetCategory;

        @Override
        protected Builder self() { return this; }

        public Builder targetCategory(String targetCategory) {
            this.targetCategory = targetCategory;
            return this;
        }

        public CategoryDiscount build() {
            this.type = DiscountType.CATEGORY;
            return new CategoryDiscount(this);
        }
    }
}
