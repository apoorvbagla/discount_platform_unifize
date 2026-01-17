# domain.org.unifize.discountplatform.src.org.unifize.discount.Discount Platform

A discount calculation engine for a fashion e-commerce platform supporting multiple discount types with configurable stacking and priority rules.

## Table of Contents
- [Overview](#overview)
- [Canonical Models](#canonical-models)
- [Constraints](#constraints)
- [Quick Start](#quick-start)

---

## Overview

This platform calculates final pricing after applying multiple discount types:
- **Brand Discounts** - Percentage off specific brands (e.g., 40% off PUMA)
- **Category Discounts** - Percentage off product categories (e.g., 10% off T-shirts)
- **Voucher Codes** - Promotional codes with constraints (exclusions, caps, tiers)
- **Payment Offers** - Bank/card-specific discounts (e.g., 10% off with ICICI)

---

## Canonical Models

### 1. Brand domain.org.unifize.discountplatform.src.org.unifize.discount.Discount

```json
{
  "id": "BRAND_PUMA_40",
  "type": "BRAND",
  "description": "40% off on all PUMA items",
  "targetBrand": "PUMA",
  "discountPercent": 40,
  "priority": 1,
  "active": true
}
```

### 2. Category domain.org.unifize.discountplatform.src.org.unifize.discount.Discount

```json
{
  "id": "CAT_TSHIRT_10",
  "type": "CATEGORY",
  "description": "Extra 10% off on all T-shirts",
  "targetCategory": "T-shirts",
  "discountPercent": 10,
  "stackable": true,
  "priority": 2,
  "active": true
}
```

### 3. Voucher Code

```json
{
  "id": "SUPER69",
  "type": "VOUCHER",
  "description": "69% off with SUPER69 code",
  "discountPercent": 69,
  "maxDiscountCap": 500,
  "excludedBrands": ["Nike"],
  "minCustomerTier": "STANDARD",
  "validFrom": "2024-01-01",
  "validTo": "2024-12-31",
  "priority": 3,
  "active": true
}
```

### 4. Payment Offer

```json
{
  "id": "ICICI_10",
  "type": "PAYMENT",
  "description": "10% instant discount on ICICI credit cards",
  "bank": "ICICI",
  "cardType": "CREDIT",
  "discountPercent": 10,
  "maxDiscountCap": 200,
  "minCartValue": 2000,
  "priority": 4,
  "active": true
}
```

---

## Constraints

### 1. How do you handle conflicting discounts?

**Approach: Stacking with Priority Order**

All applicable discounts **stack sequentially** rather than compete. Each discount applies to the **running discounted price** from the previous step.

```
Original Price → Brand domain.org.unifize.discountplatform.src.org.unifize.discount.Discount → Category domain.org.unifize.discountplatform.src.org.unifize.discount.Discount → Voucher → Payment Offer → Final Price
```

**Rationale:**
- Stacking provides better customer value and clearer reasoning
- Priority order ensures consistent, predictable results
- Each discount type operates on a different "layer" (product vs. cart vs. payment)

**Exclusion Handling:**
- Voucher exclusions (e.g., `excludedBrands: ["Nike"]`) skip specific items
- Payment offers have eligibility rules (card type, min cart value)

### 2. How do you control the order of discount application?

**Fixed Priority Pipeline:**

| Priority | Type | Scope | Rationale |
|----------|------|-------|-----------|
| 1 | Brand | Per-item | Applied first as it's the most specific product-level discount |
| 2 | Category | Per-item | Stacks on brand-discounted price for eligible categories |
| 3 | Voucher | Per-item (with exclusions) | User-initiated discount with constraints |
| 4 | Payment | domain.org.unifize.discountplatform.src.org.unifize.discount.Cart-level | Applied last to final cart total (bank partnership) |

**Implementation:**
```java
discounts.sort(Comparator.comparing(Discount::getPriority));
for (Discount discount : discounts) {
    result = applyDiscount(cart, discount, result);
}
```

### 3. How do you enforce upper thresholds on discount?

**Three-Level Cap Enforcement:**

1. **Per-Rule Cap** (`maxDiscountCap`)
   ```java
   long discountAmount = calculatePercentageDiscount(price, percent);
   if (rule.getMaxDiscountCap() != null) {
       discountAmount = Math.min(discountAmount, rule.getMaxDiscountCap());
   }
   ```

2. **Per-Item Cap** (discount cannot exceed item price)
   ```java
   discountAmount = Math.min(discountAmount, item.getCurrentPrice());
   ```

3. **Min domain.org.unifize.discountplatform.src.org.unifize.discount.Cart Value** (for payment offers)
   ```java
   if (cart.getTotal() < paymentOffer.getMinCartValue()) {
       return domain.org.unifize.discountplatform.src.org.unifize.discount.DiscountResult.skipped("domain.org.unifize.discountplatform.src.org.unifize.discount.Cart below minimum value");
   }
   ```

---

## Quick Start

### Run the Example

```bash
cd src/main/java
javac discount/**/*.java
java discount.Main
```

### Expected Output

```
=== domain.org.unifize.discountplatform.src.org.unifize.discount.Discount Calculation Result ===
Original Total: ₹6,997.00
Final Price: ₹5,377.92

Applied Discounts:
1. BRAND_PUMA_40: -₹799.20 (40% off PUMA items)
2. CAT_TSHIRT_10: -₹119.88 (10% off T-shirts)
3. SUPER69: -₹500.00 (69% off, capped at ₹500)
4. ICICI_10: -₹200.00 (10% ICICI, capped at ₹200)

Total Savings: ₹1,619.08
```

---

## Project Structure

```
discount-platform/
├── README.md                      # This file
├── docs/
│   ├── architecture.md            # System design & diagrams
│   ├── review-notes.md            # Code review improvements
│   └── testing-strategy.md        # Testing approach
└── src/
    └── main/java/discount/
        ├── domain/                # Core domain types
        ├── engine/                # domain.org.unifize.discountplatform.src.org.unifize.discount.Discount calculation logic
        └── Main.java              # Entry point with example
```
