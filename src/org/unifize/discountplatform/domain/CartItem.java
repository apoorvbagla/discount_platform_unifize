package org.unifize.discountplatform.domain;
/**
 * Represents a single item in the shopping cart.
 */
public final class CartItem {
    private final String productId;
    private final String name;
    private final String brand;
    private final String category;
    private final Money unitPrice;
    private final int quantity;

    public CartItem(String productId, String name, String brand, String category, Money unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getTotalPrice() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public String toString() {
        return String.format("%d × %s (%s) @ %s", quantity, name, brand, unitPrice);
    }
}
