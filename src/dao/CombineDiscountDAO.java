package dao;

import entity.CombineDiscount;
import entity.CustomerCombineDiscount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBManager;

public class CombineDiscountDAO {
    public List<CombineDiscount> getAllCombineDiscounts() {
        List<CombineDiscount> discounts = new ArrayList<>();
        String sql = "select * from CombineDiscount";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery())
        {
            while (rs.next()) {
                discounts.add(new CombineDiscount(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getInt("min_total_amount"),
                        rs.getInt("discount_amount"),
                        rs.getBoolean("phone_plan_required"),
                        rs.getBoolean("internet_plan_required"),
                        rs.getBoolean("tv_plan_required")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discounts;
    }

    public List<CustomerCombineDiscount> getCustomerCombineDiscounts(int customerId) {
        List<CustomerCombineDiscount> combineDiscounts = new ArrayList<>();
        String sql = "select * from CustomerCombineDiscount where customer_id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    combineDiscounts.add(new CustomerCombineDiscount(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getInt("combine_discount_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return combineDiscounts;
    }

    public void insertCustomerCombineDiscount(int customerId, int combineDiscountId) {
        String sql = "insert into CustomerCombineDiscount (customer_id, combine_discount_id) values (?, ?)";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, combineDiscountId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerCombineDiscount(int customerId, int combineDiscountId) {
        String sql = "update CustomerCombineDiscount set combine_discount_id = ? where customer_id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setInt(1, combineDiscountId);
            pstmt.setInt(2, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdateCustomerCombineDiscount(int customerId, int combineDiscountId) {
        CustomerDAO customerDAO = new CustomerDAO();
        CustomerCombineDiscount existingCombineDiscount = customerDAO.getCustomerCombineDiscount(customerId);

        if (existingCombineDiscount == null) {
            insertCustomerCombineDiscount(customerId, combineDiscountId);
        } else {
            updateCustomerCombineDiscount(customerId, combineDiscountId);
        }
    }
}
