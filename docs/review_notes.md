## Review Notes

### 1. Inappropriate Use of Design Patterns

- All types of discounts were clubbed into a single `Discount` class.
- This reduces testability and makes the system hard to extend.

**Recommended Improvement**
- Use a behavioral design pattern such as the **Strategy Pattern** to encapsulate each discount type.
- Benefits:
    - Independent testing of each discount strategy
    - Easy extensibility for new discount types

---

### 2. Issues in Discount Rule Models

#### a. Voucher Discount Rules

- Voucher discount calculation did not include a field representing the actual voucher code.

**Recommended Change**
- Add field:
    - `voucher_code`

#### b. Payment Discount Rules

- Payment discount calculation did not consider the payment mode.

**Recommended Change**
- Add field:
    - `payment_mode`

#### c. Discount Priority Handling

- Priority was defined as `1, 2, 3, 4` within `DiscountType`.
- This approach is not extensible if new priorities (e.g., sub-category level discounts) need to be inserted.

**Recommended Change**
- Use spaced priority values:
    - `1000, 2000, 3000, 4000`
- This allows insertion of intermediate priorities without refactoring existing rules.

---

### 3. Limitations in Payment Method Model

- `PaymentMethod` model only included:
    - Bank
    - Card Type
- Payment modes (e.g., UPI, Wallets) were not considered.

**Recommended Improvement**
- Use a **Factory Pattern** based on `payment_mode` to create payment handlers.
- This enables easy addition of new payment modes like UPI in the future.

---

### 4. Missing Timestamp Fields in Discount Objects

- Discount entities lacked time-based metadata.
- Administrative entities should have lifecycle timestamps.

**Recommended Change**
- Add fields:
    - `created_date_time`
    - `last_updated_date_time`

---

### 5. Mutability of Discount Objects

- Discount objects were mutable, making audits unreliable.

**Recommended Improvement**
- Make discount objects immutable.
- Use the **Builder Pattern** to:
    - Control object creation
    - Maintain reliable audit trails

---

### 6. Discount Status Handling

- Discount objects used a simple `active: true/false` flag.

**Recommended Improvement**
- Replace `active` with a `status` field.

**Possible Values**
- `DRAFT`
- `PUBLISHED`
- `DECOMMISSIONED`

> _Note: Proposed for now, not implemented._

---