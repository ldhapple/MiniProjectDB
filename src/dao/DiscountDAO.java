package dao;

import entity.Discount;
import entity.GradeDiscount;
import entity.dto.LifeDiscountInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.DBManager;

public class DiscountDAO {

    public List<LifeDiscountInfo> getLifestyleDiscountsByCustomerId(int customerId) {
        List<LifeDiscountInfo> discounts = new ArrayList<>();
        String sql = "select " +
                "c.name as category_name, " +
                "d.description as discount_description, " +
                "g.name as grade_name, " +
                "(g.base_discount_rate * gd.discount_rate) as final_discount_rate " +
                "from customer cu " +
                "join grade g on cu.Grade_id = g.id " +
                "join GradeDiscount gd on g.id = gd.Grade_id " +
                "join discount d on gd.Discount_id = d.id " +
                "join category c on d.Category_id = c.id " +
                "where cu.id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LifeDiscountInfo discount = new LifeDiscountInfo(
                            rs.getString("category_name"),
                            rs.getString("discount_description"),
                            rs.getString("grade_name"),
                            rs.getDouble("final_discount_rate")
                    );
                    discounts.add(discount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discounts;
    }

    public List<GradeDiscount> getGradeDiscounts(int gradeId) {
        List<GradeDiscount> gradeDiscounts = new ArrayList<>();
        String sql = "select * from GradeDiscount where Grade_id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, gradeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GradeDiscount gradeDiscount = new GradeDiscount(
                            rs.getInt("Grade_id"),
                            rs.getInt("Discount_id"),
                            rs.getDouble("discount_rate")
                    );
                    gradeDiscounts.add(gradeDiscount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gradeDiscounts;
    }
}
