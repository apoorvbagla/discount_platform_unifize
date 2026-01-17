package org.unifize.discountplatform.domain.strategy;

import org.unifize.discountplatform.domain.*;
import java.time.LocalDateTime;

/**
 * Base class providing common functionality for all discount strategies.
 */
public abstract class AbstractDiscountStrategy implements DiscountStrategy {

    protected final String id;
    protected final DiscountType type;
    protected final String description;
    protected final int discountPercent;
    protected final Money maxDiscountCap;
    protected final LocalDateTime createdDateTime;
    protected final LocalDateTime lastUpdatedDateTime;

    protected AbstractDiscountStrategy(AbstractBuilder<?> builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.description = builder.description;
        this.discountPercent = builder.discountPercent;
        this.maxDiscountCap = builder.maxDiscountCap;
        this.createdDateTime = builder.createdDateTime != null
                ? builder.createdDateTime : LocalDateTime.now();
        this.lastUpdatedDateTime = builder.lastUpdatedDateTime != null
                ? builder.lastUpdatedDateTime : LocalDateTime.now();
    }

    @Override
    public String getId() { return id; }

    @Override
    public DiscountType getType() { return type; }

    @Override
    public int getPriority() { return type.getPriority(); }

    @Override
    public String getDescription() { return description; }

    @Override
    public int getDiscountPercent() { return discountPercent; }

    @Override
    public Money getMaxDiscountCap() { return maxDiscountCap; }

    @Override
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }

    @Override
    public LocalDateTime getLastUpdatedDateTime() { return lastUpdatedDateTime; }

    @Override
    public String toString() {
        return String.format("%s (%s): %d%% off", id, type, discountPercent);
    }

    /**
     * Base builder for all discount strategies.
     */
    protected abstract static class AbstractBuilder<T extends AbstractBuilder<T>> {
        protected String id;
        protected DiscountType type;
        protected String description;
        protected int discountPercent;
        protected Money maxDiscountCap;
        protected LocalDateTime createdDateTime;
        protected LocalDateTime lastUpdatedDateTime;

        protected abstract T self();

        public T id(String id) { this.id = id; return self(); }
        public T description(String description) { this.description = description; return self(); }
        public T discountPercent(int discountPercent) { this.discountPercent = discountPercent; return self(); }
        public T maxDiscountCap(Money maxDiscountCap) { this.maxDiscountCap = maxDiscountCap; return self(); }
        public T createdDateTime(LocalDateTime createdDateTime) { this.createdDateTime = createdDateTime; return self(); }
        public T lastUpdatedDateTime(LocalDateTime lastUpdatedDateTime) { this.lastUpdatedDateTime = lastUpdatedDateTime; return self(); }
    }
}
