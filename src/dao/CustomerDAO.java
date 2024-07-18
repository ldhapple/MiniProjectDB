package dao;

import entity.Customer;
import entity.CustomerCombineDiscount;
import entity.Plan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.DBManager;

public class CustomerDAO {

    public List<Customer> getAllCustomer() {
        List<Customer> customers = new ArrayList<>();
        String sql = "select * from Customer";

        try (Connection con = DBManager.getConnection();
             Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql)) {
            while (result.next()) {
                Customer customer = new Customer(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getInt("age"),
                        result.getString("phone_number"),
                        result.getInt("InternetPlan_id"),
                        result.getInt("PhonePlan_id"),
                        result.getInt("TVPlan_id"),
                        result.getInt("Grade_id")
                );

                customers.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    public Customer getCustomer(int customerId) {
        Customer res = null;
        String sql = "select * from Customer where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, customerId);

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    Customer customer = new Customer(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getInt("age"),
                            result.getString("phone_number"),
                            result.getInt("InternetPlan_id"),
                            result.getInt("PhonePlan_id"),
                            result.getInt("TVPlan_id"),
                            result.getInt("Grade_id")
                    );

                    res = customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public List<Customer> getCustomersByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "select * from Customer where name like ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, "%" + name + "%");

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Customer customer = new Customer(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getInt("age"),
                            result.getString("phone_number"),
                            result.getInt("InternetPlan_id"),
                            result.getInt("PhonePlan_id"),
                            result.getInt("TVPlan_id"),
                            result.getInt("Grade_id")
                    );

                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    public int insertCustomer(Customer customer) {
        int ret = -1;
        String sql = "insert into Customer VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setInt(1, customer.id());
            pstmt.setString(2, customer.name());
            pstmt.setInt(3, customer.age());
            pstmt.setString(4, customer.phoneNumber());
            pstmt.setInt(5, customer.gradeId());
            if (customer.internetPlanId() != null) {
                pstmt.setInt(6, customer.internetPlanId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            if (customer.phonePlanId() != null) {
                pstmt.setInt(7, customer.phonePlanId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            if (customer.tvPlanId() != null) {
                pstmt.setInt(8, customer.tvPlanId());
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }

            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public int updateCustomer(Customer customer) {
        int ret = -1;
        String sql = "update Customer set name = ?, age = ?, phone_number = ?, "
                + "InternetPlan_id = ?, PhonePlan_id = ?, TVPlan_id = ?, Grade_id = ? WHERE id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setString(1, customer.name());
            pstmt.setInt(2, customer.age());
            pstmt.setString(3, customer.phoneNumber());
            pstmt.setInt(4, customer.internetPlanId());
            pstmt.setInt(5, customer.phonePlanId());
            pstmt.setInt(6, customer.tvPlanId());
            pstmt.setInt(7, customer.gradeId());
            pstmt.setInt(8, customer.id());

            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int deleteCustomer(int id) {
        int ret = -1;
        String sql = "delete from Customer where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setInt(1, id);
            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void updateCustomerPlan(int customerId, String planType, Integer planId) {
        String sql = null;
        switch (planType) {
            case "PhonePlan":
                sql = "update Customer set PhonePlan_id = ? where id = ?";
                break;
            case "InternetPlan":
                sql = "update Customer set InternetPlan_id = ? where id = ?";
                break;
            case "TVPlan":
                sql = "update Customer set TVPlan_id = ? where id = ?";
                break;
        }

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            if (planId != null) {
                pstmt.setInt(1, planId);
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }
            pstmt.setInt(2, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerGrade(int customerId, int gradeId) {
        String sql = "update Customer set grade_id = ? where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, gradeId);
            pstmt.setInt(2, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalAmount(int customerId) {
        int totalAmount = 0;
        String sql = "select sum(price) as total_amount from ( " +
                "select price from PhonePlan where id = (select phonePlan_id from Customer where id = ?) " +
                "UNION ALL " +
                "SELECT price from InternetPlan where id = (select internetPlan_id from Customer where id = ?) " +
                "UNION ALL " +
                "SELECT price from TVPlan where id = (select TVPlan_id from Customer where id = ?) " +
                ") as total";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalAmount = rs.getInt("total_amount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalAmount;
    }

    public CustomerCombineDiscount getCustomerCombineDiscount(int customerId) {
        CustomerCombineDiscount combineDiscount = null;
        String sql = "select * from CustomerCombineDiscount where customer_id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    combineDiscount = new CustomerCombineDiscount(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getInt("combine_discount_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return combineDiscount;
    }
}
