package dao;

import entity.Plan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.DBManager;

public class PlanDAO {

    // 3가지 요금제 클래스를 만드는 것 보다, tableName으로 Phone, Internet, TV를 구분.
    public Plan getPlan(int planId, String tableName) {
        Plan plan = null;
        String sql = "select * from " + tableName + " where id = ?; ";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setInt(1, planId);

            try (ResultSet result = pstmt.executeQuery())
            {
                if (result.next()) {
                    plan = new Plan(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getInt("price"),
                            result.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plan;
    }

    public List<Plan> getAllPlans(String tableName) {
        List<Plan> plans = new ArrayList<>();
        String sql = "select * from " + tableName;

        try (Connection con = DBManager.getConnection();
             Statement stmt = con.createStatement();
             ResultSet result = stmt.executeQuery(sql))
        {
            while (result.next()) {
                Plan plan = new Plan(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getInt("price"),
                        result.getString("description")
                );
                plans.add(plan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plans;
    }

    public Plan getPlanById(int planId, String tableName) {
        Plan plan = null;
        String sql = "select * from " + tableName + " where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setInt(1, planId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    plan = new Plan(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("price"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plan;
    }

    public Plan getPlanByName(String planName, String tableName) {
        Plan plan = null;
        String sql = "select * from " + tableName + " where name = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql))
        {
            pstmt.setString(1, planName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    plan = new Plan(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("price"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plan;
    }

    public int insertPlan(Plan plan, String tableName) {
        int ret = -1;
        String sql = "insert into " + tableName + " values (?, ?, ?, ?)";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, plan.id());
            ps.setString(2, plan.name());
            ps.setInt(3, plan.price());
            ps.setString(4, plan.description());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int updatePlan(Plan plan, String tableName) {
        int ret = -1;
        String sql = "update " + tableName + " set name = ?, price = ?, description = ? where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, plan.name());
            ps.setInt(2, plan.price());
            ps.setString(3, plan.description());
            ps.setInt(4, plan.id());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public int deletePlan(int planId, String tableName) {
        int ret = -1;
        String sql = "delete from " + tableName + " where id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, planId);
            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
