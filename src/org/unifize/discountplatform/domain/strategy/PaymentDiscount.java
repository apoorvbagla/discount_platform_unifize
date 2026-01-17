package org.unifize.discountplatform.domain.strategy;

import org.unifize.discountplatform.domain.*;
import org.unifize.discountplatform.domain.payment.*;
import java.util.*;

/**
 * Strategy for payment method-based discounts.
 * Supports various payment modes including cards, UPI, and wallets.
 */
public final class PaymentDiscount extends AbstractDiscountStrategy {

    private final PaymentMode paymentMode;
    private final String requiredBank;
    private final String requiredCardType;
    private final String requiredUpiApp;
    private final String requiredWalletProvider;
    private final Money minCartValue;

    private PaymentDiscount(Builder builder) {
        super(builder);
        this.paymentMode = builder.paymentMode;
        this.requiredBank = builder.requiredBank;
        this.requiredCardType = builder.requiredCardType;
        this.requiredUpiApp = builder.requiredUpiApp;
        this.requiredWalletProvider = builder.requiredWalletProvider;
        this.minCartValue = builder.minCartValue;
    }

    public PaymentMode getPaymentMode() { return paymentMode; }
    public String getRequiredBank() { return requiredBank; }
    public String getRequiredCardType() { return requiredCardType; }
    public String getRequiredUpiApp() { return requiredUpiApp; }
    public String getRequiredWalletProvider() { return requiredWalletProvider; }
    public Money getMinCartValue() { return minCartValue; }

    @Override
    public Money apply(Cart cart, Map<CartItem, Money> itemPrices,
                       DiscountResult.Builder resultBuilder) {
        PaymentMethod payment = cart.getPaymentMethod();

        // Check payment method exists
        if (payment == null) {
            resultBuilder.addSkippedReason(id + ": No payment method specified");
            return Money.zero();
        }

        // Build criteria and check match
        PaymentMethodCriteria criteria = PaymentMethodCriteria.builder()
                .requiredMode(paymentMode)
                .requiredBank(requiredBank)
                .requiredCardType(requiredCardType)
                .requiredUpiApp(requiredUpiApp)
                .requiredWalletProvider(requiredWalletProvider)
                .build();

        if (!payment.matches(criteria)) {
            resultBuilder.addSkippedReason(String.format(
                    "%s: Payment method %s doesn't match required criteria",
                    id, payment));
            return Money.zero();
        }

        // Calculate current cart total
        Money currentTotal = itemPrices.values().stream()
                .reduce(Money.zero(), Money::add);

        // Check minimum cart value
        if (minCartValue != null && currentTotal.isLessThan(minCartValue)) {
            resultBuilder.addSkippedReason(String.format(
                    "%s: Cart total %s below minimum %s",
                    id, currentTotal, minCartValue));
            return Money.zero();
        }

        // Calculate discount on total
        Money paymentDiscount = currentTotal.percentage(discountPercent);

        // Apply cap
        if (maxDiscountCap != null) {
            paymentDiscount = paymentDiscount.min(maxDiscountCap);
        }

        // Distribute discount proportionally across items
        if (paymentDiscount.isGreaterThan(Money.zero())) {
            distributeDiscountProportionally(itemPrices, currentTotal, paymentDiscount);
            resultBuilder.appendReasoning(String.format(
                    "  %s: %d%% payment discount (%s) = %s (capped at %s)",
                    id, discountPercent, paymentMode, paymentDiscount, maxDiscountCap));
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
                itemShare = remaining;
            } else {
                double ratio = (double) itemPrice.getPaise() / currentTotal.getPaise();
                itemShare = Money.ofPaise(Math.round(discountAmount.getPaise() * ratio));
            }

            itemShare = itemShare.min(itemPrice);
            itemPrices.put(item, itemPrice.subtract(itemShare));
            remaining = remaining.subtract(itemShare);
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder extends AbstractBuilder<Builder> {
        private PaymentMode paymentMode;
        private String requiredBank;
        private String requiredCardType;
        private String requiredUpiApp;
        private String requiredWalletProvider;
        private Money minCartValue;

        @Override
        protected Builder self() { return this; }

        public Builder paymentMode(PaymentMode paymentMode) {
            this.paymentMode = paymentMode;
            return this;
        }

        public Builder requiredBank(String requiredBank) {
            this.requiredBank = requiredBank;
            return this;
        }

        public Builder requiredCardType(String requiredCardType) {
            this.requiredCardType = requiredCardType;
            return this;
        }

        public Builder requiredUpiApp(String requiredUpiApp) {
            this.requiredUpiApp = requiredUpiApp;
            return this;
        }

        public Builder requiredWalletProvider(String requiredWalletProvider) {
            this.requiredWalletProvider = requiredWalletProvider;
            return this;
        }

        public Builder minCartValue(Money minCartValue) {
            this.minCartValue = minCartValue;
            return this;
        }

        public PaymentDiscount build() {
            this.type = DiscountType.PAYMENT;
            return new PaymentDiscount(this);
        }
    }
}
