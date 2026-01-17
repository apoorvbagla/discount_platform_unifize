package org.unifize.discountplatform.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of discount calculation containing final price, applied discounts, and reasoning.
 */
public final class DiscountResult {
    private final Money originalTotal;
    private final Money finalPrice;
    private final List<AppliedDiscount> appliedDiscounts;
    private final List<String> skippedReasons;
    private final String reasoning;

    private DiscountResult(Money originalTotal, Money finalPrice,
                          List<AppliedDiscount> appliedDiscounts,
                          List<String> skippedReasons,
                          String reasoning) {
        this.originalTotal = originalTotal;
        this.finalPrice = finalPrice;
        this.appliedDiscounts = new ArrayList<>(appliedDiscounts);
        this.skippedReasons = new ArrayList<>(skippedReasons);
        this.reasoning = reasoning;
    }

    public Money getOriginalTotal() {
        return originalTotal;
    }

    public Money getFinalPrice() {
        return finalPrice;
    }

    public List<AppliedDiscount> getAppliedDiscounts() {
        return Collections.unmodifiableList(appliedDiscounts);
    }

    public List<String> getSkippedReasons() {
        return Collections.unmodifiableList(skippedReasons);
    }

    public String getReasoning() {
        return reasoning;
    }

    public Money getTotalSavings() {
        return originalTotal.subtract(finalPrice);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Money originalTotal = Money.zero();
        private Money finalPrice = Money.zero();
        private final List<AppliedDiscount> appliedDiscounts = new ArrayList<>();
        private final List<String> skippedReasons = new ArrayList<>();
        private final StringBuilder reasoningBuilder = new StringBuilder();

        public Builder originalTotal(Money originalTotal) {
            this.originalTotal = originalTotal;
            return this;
        }

        public Builder finalPrice(Money finalPrice) {
            this.finalPrice = finalPrice;
            return this;
        }

        public Builder addAppliedDiscount(AppliedDiscount discount) {
            this.appliedDiscounts.add(discount);
            return this;
        }

        public Builder addSkippedReason(String reason) {
            this.skippedReasons.add(reason);
            return this;
        }

        public Builder appendReasoning(String text) {
            if (reasoningBuilder.length() > 0) {
                reasoningBuilder.append("\n");
            }
            reasoningBuilder.append(text);
            return this;
        }

        public DiscountResult build() {
            return new DiscountResult(originalTotal, finalPrice, appliedDiscounts,
                    skippedReasons, reasoningBuilder.toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Discount Calculation Result ===\n");
        sb.append("Original Total: ").append(originalTotal).append("\n");
        sb.append("Final Price: ").append(finalPrice).append("\n\n");

        if (!appliedDiscounts.isEmpty()) {
            sb.append("Applied Discounts:\n");
            int i = 1;
            for (AppliedDiscount discount : appliedDiscounts) {
                sb.append(i++).append(". ").append(discount).append("\n");
            }
        }

        if (!skippedReasons.isEmpty()) {
            sb.append("\nSkipped Discounts:\n");
            for (String reason : skippedReasons) {
                sb.append("- ").append(reason).append("\n");
            }
        }

        sb.append("\nTotal Savings: ").append(getTotalSavings());
        return sb.toString();
    }
}
