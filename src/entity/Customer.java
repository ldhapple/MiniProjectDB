package entity;

public record Customer (
        int id,
        String name,
        int age,
        String phoneNumber,
        Integer internetPlanId,
        Integer phonePlanId,
        Integer tvPlanId,
        int gradeId
) { }
