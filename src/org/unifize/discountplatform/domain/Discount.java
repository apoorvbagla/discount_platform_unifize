package org.unifize.discountplatform.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a discount rule with its configuration.
 */
public final class Discount {
    private final String id;
    private final DiscountType type;
    private final String description;
    private final int discountPercent;

    // For BRAND discounts
    private final String targetBrand;

    // For CATEGORY discounts
    private final String targetCategory;

    // For VOUCHER discounts
    private final Set<String> excludedBrands;
    private final String minCustomerTier;

    // For PAYMENT discounts
    private final String requiredBank;
    private final String requiredCardType;
    private final Money minCartValue;

    // Common constraints
    private final Money maxDiscountCap;

    private Discount(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.description = builder.description;
        this.discountPercent = builder.discountPercent;
        this.targetBrand = builder.targetBrand;
        this.targetCategory = builder.targetCategory;
        this.excludedBrands = builder.excludedBrands != null
                ? new HashSet<>(builder.excludedBrands)
                : Collections.emptySet();
        this.minCustomerTier = builder.minCustomerTier;
        this.requiredBank = builder.requiredBank;
        this.requiredCardType = builder.requiredCardType;
        this.minCartValue = builder.minCartValue;
        this.maxDiscountCap = builder.maxDiscountCap;
    }

    public String getId() { return id; }
    public DiscountType getType() { return type; }
    public String getDescription() { return description; }
    public int getDiscountPercent() { return discountPercent; }
    public String getTargetBrand() { return targetBrand; }
    public String getTargetCategory() { return targetCategory; }
    public Set<String> getExcludedBrands() { return Collections.unmodifiableSet(excludedBrands); }
    public String getMinCustomerTier() { return minCustomerTier; }
    public String getRequiredBank() { return requiredBank; }
    public String getRequiredCardType() { return requiredCardType; }
    public Money getMinCartValue() { return minCartValue; }
    public Money getMaxDiscountCap() { return maxDiscountCap; }

    public int getPriority() {
        return type.getPriority();
    }

    public boolean isBrandExcluded(String brand) {
        return excludedBrands.stream()
                .anyMatch(excluded -> excluded.equalsIgnoreCase(brand));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private DiscountType type;
        private String description;
        private int discountPercent;
        private String targetBrand;
        private String targetCategory;
        private Set<String> excludedBrands;
        private String minCustomerTier;
        private String requiredBank;
        private String requiredCardType;
        private Money minCartValue;
        private Money maxDiscountCap;

        public Builder id(String id) { this.id = id; return this; }
        public Builder type(DiscountType type) { this.type = type; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder discountPercent(int discountPercent) { this.discountPercent = discountPercent; return this; }
        public Builder targetBrand(String targetBrand) { this.targetBrand = targetBrand; return this; }
        public Builder targetCategory(String targetCategory) { this.targetCategory = targetCategory; return this; }
        public Builder excludedBrands(Set<String> excludedBrands) { this.excludedBrands = excludedBrands; return this; }
        public Builder minCustomerTier(String minCustomerTier) { this.minCustomerTier = minCustomerTier; return this; }
        public Builder requiredBank(String requiredBank) { this.requiredBank = requiredBank; return this; }
        public Builder requiredCardType(String requiredCardType) { this.requiredCardType = requiredCardType; return this; }
        public Builder minCartValue(Money minCartValue) { this.minCartValue = minCartValue; return this; }
        public Builder maxDiscountCap(Money maxDiscountCap) { this.maxDiscountCap = maxDiscountCap; return this; }

        public Discount build() {
            return new Discount(this);
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %d%% off", id, type, discountPercent);
    }
}
