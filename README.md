# E-commerce Discount Platform

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

### 1. Brand Discount

```json
{
  "id": "BRAND_PUMA_40",
  "type": "BRAND",
  "description": "40% off on all PUMA items",
  "targetBrand": "PUMA",
  "discountPercent": 40,
  "priority": 1000,
  "active": true,
  "created_date_time": 1768642127,
  "last_updated_date_time": 1768642127
}
```

### 2. Category Discount

```json
{
  "id": "CAT_TSHIRT_10",
  "type": "CATEGORY",
  "description": "Extra 10% off on all T-shirts",
  "targetCategory": "tshirts",
  "discountPercent": 10,
  "stackable": true,
  "priority": 2000,
  "active": true,
  "created_date_time": 1768642127,
  "last_updated_date_time": 1768642127
}
```

### 3. Voucher Code

```json
{
  "id": "SUPER69",
  "type": "VOUCHER",
  "voucher_code": "SUPER69",
  "description": "69% off with SUPER69 code",
  "discountPercent": 69,
  "maxDiscountCap": 500,
  "excludedBrands": ["Nike"],
  "minCustomerTier": "STANDARD",
  "validFrom": "2024-01-01",
  "validTo": "2024-12-31",
  "priority": 3000,
  "active": true,
  "created_date_time": 1768642127,
  "last_updated_date_time": 1768642127
}
```

### 4. Payment Offer

```json
{
  "id": "ICICI_10",
  "type": "PAYMENT",
  "payment_mode": "credit_card",
  "description": "10% instant discount on ICICI credit cards",
  "bank": "ICICI",
  "cardType": "CREDIT",
  "discountPercent": 10,
  "maxDiscountCap": 200,
  "minCartValue": 2000,
  "priority": 4000,
  "active": true,
  "created_date_time": 1768642127,
  "last_updated_date_time": 1768642127
}
```

---

## Constraints

### 1. How do you handle conflicting discounts?

#### Non-stackable Discounts
- Only the highest-value discount at that level is applied.

#### Explicit Exclusions
- Explicit exclusions (e.g. *Nike excluded from SUPER69*) are evaluated per cart item.

#### Conflicting brand or category discounts
- This can be handled probably by last_updated_date_time where we fetch the latest updated discount and ofcourse is active

### 2. How do you control the order of discount application?

Discounts are applied in an explicit priority-based order.

#### Order of Application
- Brand discounts
- Category discounts
- Voucher discounts
- Payment offers

#### Why
- Item-level discounts should reduce the base price first.
- Vouchers operate on the already-discounted subtotal.
- Payment offers apply last on the payable amount.

This is enforced using a `priority` field on each rule type.

### 3. How do you enforce upper thresholds on discount?

Each discount calculation:
- Compute raw discount
- Apply `min(rawDiscount, maxCap)`
- Record both values for transparency in reasoning

---
## Architecture

Please refer to docs/architecture.pdf for the diagram and other details


