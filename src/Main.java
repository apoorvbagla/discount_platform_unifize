import org.unifize.discountplatform.domain.*;
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

        // Create payment method (ICICI Credit Card)
        PaymentMethod payment = new PaymentMethod("ICICI", "CREDIT");

        // Create cart
        Cart cart = new Cart("cart-123", items, payment, "cust-456", "STANDARD");

        // Create discounts from the assignment
        List<Discount> discounts = createDiscounts();

        // Calculate discounts
        DiscountCalculator calculator = new DiscountCalculator();
        DiscountResult result = calculator.calculateDiscounts(cart, discounts);

        // Print result
        System.out.println(result);

        System.out.println("\n=== Detailed Reasoning ===");
        System.out.println(result.getReasoning());
    }

    private static List<Discount> createDiscounts() {
        return Arrays.asList(
                // 1. Brand discount: 40% off PUMA
                Discount.builder()
                        .id("BRAND_PUMA_40")
                        .type(DiscountType.BRAND)
                        .description("40% off PUMA items")
                        .discountPercent(40)
                        .targetBrand("PUMA")
                        .build(),

                // 2. Category discount: 10% off T-shirts (stackable)
                Discount.builder()
                        .id("CAT_TSHIRT_10")
                        .type(DiscountType.CATEGORY)
                        .description("10% off T-shirts")
                        .discountPercent(10)
                        .targetCategory("T-shirts")
                        .build(),

                // 3. Voucher: SUPER69 - 69% off, excludes Nike, max cap ₹500
                Discount.builder()
                        .id("SUPER69")
                        .type(DiscountType.VOUCHER)
                        .description("69% off with SUPER69 voucher")
                        .discountPercent(69)
                        .excludedBrands(new HashSet<>(Arrays.asList("Nike")))
                        .maxDiscountCap(Money.ofRupees(500))
                        .build(),

                // 4. Payment offer: 10% ICICI, max ₹200, min cart ₹2000
                Discount.builder()
                        .id("ICICI_10")
                        .type(DiscountType.PAYMENT)
                        .description("10% instant discount on ICICI credit cards")
                        .discountPercent(10)
                        .requiredBank("ICICI")
                        .requiredCardType("CREDIT")
                        .maxDiscountCap(Money.ofRupees(200))
                        .minCartValue(Money.ofRupees(2000))
                        .build()
        );
    }
}
