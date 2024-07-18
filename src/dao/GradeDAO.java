package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBManager;

public class GradeDAO {

    public String getGradeName(int gradeId) {
        String grade = "";
        String sql = "select name from grade where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);)
        {
            pstmt.setInt(1, gradeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    grade = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grade;
    }

    public double getDiscountRate(int gradeId) {
        double rate = 1;

        String sql = "select name from grade where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);)
        {
            pstmt.setInt(1, gradeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rate = rs.getDouble("baseDiscountRate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rate;
    }
}
