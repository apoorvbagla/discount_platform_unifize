# Testing Strategy

The objective of testing this system is to validate **correct monetary outcomes**, **deterministic discount behavior**, and **clear explainability**, while deliberately avoiding low-value or redundant tests.

---

## 1. What MUST Be Unit Tested

These areas contain **core business logic** and directly impact revenue and customer trust.

- **Discount eligibility logic**  
  Brand/category matching, voucher exclusions, minimum cart value checks.

- **Discount application order**  
  Ensures discounts are applied in the correct sequence  
  *(Brand → Category → Voucher → Payment)*.

- **Discount capping and thresholds**  
  Raw discount vs maximum cap enforcement.

- **Conflict resolution for non-stackable discounts**  
  Guarantees only the highest-value eligible discount is applied.

---

## 2. What Needs Integration Testing

Integration tests validate **interaction correctness between components**, not internal logic.

- **Cart Service + Pricing Service + Discount Engine flow**  
  Full cart pricing with multiple discount types applied together.

- **Checkout Service + Pricing Service + Discount Engine flow**  
  Pricing after selecting payment mode.

- **Checkout price revalidation**  
  Ensures pricing is recomputed and validated before order placement.

---

## 3. What We Would NOT Test (and Why)

To avoid wasteful over-testing, the following are intentionally excluded:

- **Framework and infrastructure behavior**  
  HTTP routing, JSON serialization, cache TTL eviction, and database CRUD.  
  These are responsibilities of well-tested external libraries.

- **Persistence of discount rules**  
  Rule storage is treated as configuration rather than business logic.  
  Testing rule evaluation gives higher confidence than testing storage mechanics.

- **Exhaustive discount permutations**  
  Testing every possible combination of discounts has diminishing returns.  
  Representative edge cases provide better coverage with lower maintenance cost.

> The focus is on testing **business decisions**, not implementation mechanics.

---

## 4. Pseudocode Test Cases (Edge-Case Focus)

### Voucher Exclusion with Maximum Cap

```pseudo
test "voucher applies only to eligible items and respects cap" {
  cart = [
    { brand: "PUMA", price: 1000, qty: 1 },
    { brand: "NIKE", price: 5000, qty: 1 }
  ]

  voucher = SUPER69 (69% off, excludes NIKE, maxCap = 500)

  result = calculatePrice(cart, voucher)

  assert discountAppliedOn("PUMA") == true
  assert discountAppliedOn("NIKE") == false
  assert totalVoucherDiscount == 500
}
