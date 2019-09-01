package student;

import operations.BuyerOperations;
import student.DBConnector.mz150346_DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mz150346_Buyer implements BuyerOperations {
    @Override
    public int createBuyer(String name, int cityId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "insert into Buyer(Name, Credit, CityId) values(?, ?, ?)";
        ResultSet rs = null;

        try {
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setInt(2, 0);
            ps.setInt(3, cityId);

            ps.execute();
            rs = ps.getGeneratedKeys();
            rs.next();
            return  rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int setCity(int buyerId, int cityId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "update Buyer set cityId = ? where Id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, cityId);
            ps.setInt(2, buyerId);
            ps.executeUpdate();

            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getCity(int buyerId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select cityId from Buyer where Id = ?";
        ResultSet rs = null;
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, buyerId);

            rs =  ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public BigDecimal getCredit(int buyerId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select credit from buyer where Id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            BigDecimal bd = rs.getBigDecimal(1);
            if(bd == null) return new BigDecimal(0).setScale(3);
            return  bd.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "update Buyer set credit = ? where Id = ?";

        try {
            BigDecimal currentCredit = getCredit(buyerId);
            credit = credit.add(currentCredit);

            PreparedStatement ps = con.prepareStatement(query);
            ps.setBigDecimal(1, credit);
            ps.setInt(2, buyerId);
            ps.executeUpdate();

            return credit;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int createOrder(int buyerId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "insert into Orderr(State, buyerId, Location) values ('created', ?, -1)";

        try {
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, buyerId);

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Integer> getOrders(int buyerId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from Orderr where buyerId = ?";
        ArrayList<Integer> l = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

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
