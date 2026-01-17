import org.unifize.discountplatform.domain.*;
import org.unifize.discountplatform.domain.payment.*;
import org.unifize.discountplatform.domain.strategy.*;
import org.unifize.discountplatform.engine.DiscountCalculator;

import java.util.*;

/**
 * Demonstrates the discount calculation with the example from the assignment.
 */
public class Main {

    public static void main(String[] args) {
        // Create cart items from the assignment example
        List<CartItem> items = Arrays.asList(
                new CartItem("PUMA-001", "PUMA T-shirt", "PUMA", "T-shirts",
                        Money.ofRupees(999), 2),
                new CartItem("NIKE-001", "Nike Shoes", "Nike", "Footwear",
                        Money.ofRupees(4999), 1)
        );

        // Create payment method using factory (NEW - Factory Pattern)
        PaymentMethod payment = PaymentMethodFactory.createCreditCard("ICICI", "VISA");

        // Create cart
        Cart cart = new Cart("cart-123", items, payment, "cust-456", "STANDARD");

        // Create discount strategies (NEW - Strategy Pattern)
        List<DiscountStrategy> discounts = createDiscountStrategies();

        // Calculate discounts
        DiscountCalculator calculator = new DiscountCalculator();
        DiscountResult result = calculator.calculateDiscounts(cart, discounts);

        // Print result
        System.out.println(result);

        System.out.println("\n=== Detailed Reasoning ===");
        System.out.println(result.getReasoning());
    }

    private static List<DiscountStrategy> createDiscountStrategies() {
        return Arrays.asList(
                // 1. Brand discount: 40% off PUMA
                BrandDiscount.builder()
                        .id("BRAND_PUMA_40")
                        .description("40% off PUMA items")
                        .discountPercent(40)
                        .targetBrand("PUMA")
                        .build(),

                // 2. Category discount: 10% off T-shirts (stackable)
                CategoryDiscount.builder()
                        .id("CAT_TSHIRT_10")
                        .description("10% off T-shirts")
                        .discountPercent(10)
                        .targetCategory("T-shirts")
                        .build(),

                // 3. Voucher: SUPER69 - 69% off, excludes Nike, max cap Rs.500
                // NEW: Now includes voucherCode field
                VoucherDiscount.builder()
                        .id("SUPER69")
                        .voucherCode("SUPER69")
                        .description("69% off with SUPER69 voucher")
                        .discountPercent(69)
                        .excludedBrands(new HashSet<>(Arrays.asList("Nike")))
                        .maxDiscountCap(Money.ofRupees(500))
                        .build(),

                // 4. Payment offer: 10% ICICI, max Rs.200, min cart Rs.2000
                // NEW: Now includes paymentMode field
                PaymentDiscount.builder()
                        .id("ICICI_10")
                        .paymentMode(PaymentMode.CREDIT_CARD)
                        .description("10% instant discount on ICICI credit cards")
                        .discountPercent(10)
                        .requiredBank("ICICI")
                        .maxDiscountCap(Money.ofRupees(200))
                        .minCartValue(Money.ofRupees(2000))
                        .build()
        );
    }
}
