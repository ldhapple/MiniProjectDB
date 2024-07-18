package entity;

public record GradeDiscount(
        int gradeId,
        int discountId,
        double discountRate
) {}
