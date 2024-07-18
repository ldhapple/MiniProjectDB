package entity;

public record CombineDiscount(
        int id,
        String description,
        int minTotalAmount,
        int discountAmount,
        boolean phonePlanRequired,
        boolean internetPlanRequired,
        boolean tvPlanRequired
) {}
