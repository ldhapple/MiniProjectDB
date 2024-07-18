package entity.dto;

public record LifeDiscountInfo (
        String categoryName,
        String discountDescription,
        String gradeName,
        double finalDiscountRate
){}
