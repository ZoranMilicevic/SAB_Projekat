package student;

import operations.ShopOperations;
import student.DBConnector.mz150346_DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mz150346_Shop implements ShopOperations {
    @Override
    public int createShop(String name, String cityName) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "select id from City where Name = ?";
        String query2 = "insert into Shop(Discount, CityId) values(?, ?)";

        try {
            PreparedStatement ps1 = con.prepareStatement(query1);
            ps1.setString(1, cityName);

            ResultSet rs = ps1.executeQuery();
            rs.next();
            int id = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS);
            ps2.setInt(1, 0);
            ps2.setInt(2, id);

            ps2.execute();
            ResultSet rs2 = ps2.getGeneratedKeys();
            rs2.next();
            return rs2.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int setCity(int shopId, String cityName) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "select id from City where Name = ?";
        String query2 = "update Shop set cityId = ? where id = ?";

        try {
            PreparedStatement ps1 = con.prepareStatement(query1);
            ps1.setString(1, cityName);

            ResultSet rs = ps1.executeQuery();
            rs.next();
            int id = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(query2);
            ps2.setInt(1, id);
            ps2.setInt(2, shopId);
            ps2.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getCity(int shopId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select cityId from Shop where id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int setDiscount(int shopId, int discountPercentage) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "update Shop set Discount = ? where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, discountPercentage);
            ps.setInt(2, shopId);
            ps.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getDiscount(int shopId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select discount from Shop  where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getArticleCount(int articleId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select amount from Article where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int increaseArticleCount(int articleId, int increment) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query2 = "update Article set amount = ? where id = ?";

        try {
            int amount = getArticleCount(articleId) + increment;
            PreparedStatement ps = con.prepareStatement(query2);
            ps.setInt(1, amount);
            ps.setInt(2, articleId);
            ps.executeUpdate();

            return amount;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Integer> getArticles(int shopId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from Article where shopId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();

            ArrayList<Integer> l = new ArrayList<>();
            while(rs.next() != false){
                l.add(rs.getInt(1));
            }
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
