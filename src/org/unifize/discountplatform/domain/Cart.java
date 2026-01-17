package org.unifize.discountplatform.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a shopping cart with items and payment information.
 */
public final class Cart {
    private final String cartId;
    private final List<CartItem> items;
    private final PaymentMethod paymentMethod;
    private final String customerId;
    private final String customerTier;

    public Cart(String cartId, List<CartItem> items, PaymentMethod paymentMethod,
                String customerId, String customerTier) {
        this.cartId = cartId;
        this.items = new ArrayList<>(items);
        this.paymentMethod = paymentMethod;
        this.customerId = customerId;
        this.customerTier = customerTier;
    }

    public String getCartId() {
        return cartId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerTier() {
        return customerTier;
    }

    public Money getOriginalTotal() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(Money.zero(), Money::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
