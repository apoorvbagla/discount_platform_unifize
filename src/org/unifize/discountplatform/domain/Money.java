package org.unifize.discountplatform.domain;

/**
 * Value object representing monetary amounts in paise (1/100 of a rupee).
 * Using long to avoid floating-point precision issues.
 */
public final class Money {
    private final long paise;

    private Money(long paise) {
        this.paise = paise;
    }

    public static Money ofPaise(long paise) {
        return new Money(paise);
    }

    public static Money ofRupees(double rupees) {
        return new Money(Math.round(rupees * 100));
    }

    public static Money zero() {
        return new Money(0);
    }

    public long getPaise() {
        return paise;
    }

    public double toRupees() {
        return paise / 100.0;
    }

    public Money add(Money other) {
        return new Money(this.paise + other.paise);
    }

    public Money subtract(Money other) {
        return new Money(this.paise - other.paise);
    }

    public Money multiply(int quantity) {
        return new Money(this.paise * quantity);
    }

    public Money percentage(int percent) {
        return new Money((this.paise * percent) / 100);
    }

    public Money min(Money other) {
        return this.paise <= other.paise ? this : other;
    }

    public boolean isGreaterThan(Money other) {
        return this.paise > other.paise;
    }

    public boolean isLessThan(Money other) {
        return this.paise < other.paise;
    }

    @Override
    public String toString() {
        return String.format("₹%.2f", toRupees());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return paise == money.paise;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(paise);
    }
}
