package student;

import operations.TransactionOperations;
import student.DBConnector.mz150346_DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class mz150346_Transaction implements TransactionOperations {
    @Override
    public BigDecimal getBuyerTransactionsAmmount(int buyerId) {    //total amount of money that buyer spent
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select sum(t.amount) from TransactionBuyer t, Orderr o where t.orderId = o.id and o.buyerId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();

            rs.next();
            BigDecimal bd = rs.getBigDecimal(1);
            if(bd == null) return new BigDecimal(0).setScale(3);
            return bd.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {      //total amount of money that shop received
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select sum(amount) from TransactionSystem where shopId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();

            rs.next();
            BigDecimal bd = rs.getBigDecimal(1);
            if(bd == null)return new BigDecimal(0).setScale(3);
            return bd.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Integer> getTransationsForBuyer(int buyerId) {  //all transactions that buyer made
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select t.id from TransactionBuyer t, Orderr o where t.orderId = o.id and o.buyerId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> l = new ArrayList<>();
            while(rs.next()!=false){
                l.add(rs.getInt(1));
            }
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getTransactionForBuyersOrder(int orderId) {  //all transactions buyer made for that order
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from TransactionBuyer where orderId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getTransactionForShopAndOrder(int orderId, int shopId) {     //get transactions that shop received for that order
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from TransactionSystem where orderId = ? and ShopId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            ps.setInt(2, shopId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Integer> getTransationsForShop(int shopId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from TransactionSystem where shopId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> l = new ArrayList<>();
            while(rs.next()!=false){
                l.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select amount from TransactionBuyer where orderId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            BigDecimal bd = rs.getBigDecimal(1);
            if(bd == null) return new BigDecimal(0).setScale(3);
            return bd.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select amount from TransactionSystem where shopId = ? and orderId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, shopId);
            ps.setInt(2, orderId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            BigDecimal bd = rs.getBigDecimal(1);
            if(bd == null) return new BigDecimal(0).setScale(3);
            return bd.setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getTransactionAmount(int transactionId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "select amount from TransactionBuyer where id = ?";
        String query2 = "select amount from TransactionSystem where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query1);
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()!=false){
                BigDecimal bd = rs.getBigDecimal(1);
                if(bd == null) return new BigDecimal(0).setScale(3);
                return bd.setScale(3);
            }
            else {
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps.setInt(1, transactionId);
                ResultSet rs2 = ps.executeQuery();
                rs2.next();
                BigDecimal bd = rs.getBigDecimal(1);
                if(bd == null) return new BigDecimal(0).setScale(3);
                return bd.setScale(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Calendar getTimeOfExecution(int transactionId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "select sentTime from Orderr o, TransactionBuyer t where t.id = ? and t.orderid = o.id";
        String query2 = "select recievedTime from Orderr o, TransactionSystem t where t.id = ? and t.orderid = o.id";
        try {
            PreparedStatement ps = con.prepareStatement(query1);
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()!=false) {
                java.sql.Date d = rs.getDate(1);
                if(d==null)return null;
                Calendar cal = new GregorianCalendar();
                cal.setTime(d);
                return cal;
            }
            else {
                PreparedStatement ps2 = con.prepareStatement(query2);
                ps2.setInt(1, transactionId);
                ResultSet rs2 = ps2.executeQuery();
                rs2.next();
                java.sql.Date d = rs2.getDate(1);
                if(d==null)return null;
                Calendar cal = new GregorianCalendar();
                cal.setTime(d);
                return cal;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getSystemProfit() {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "select sum(tb.amount) from TransactionBuyer tb, Orderr o where tb.orderId = o.id and o.recievedTime is not null";
        String query2 = "select sum(ts.amount) from TransactionSystem ts";

        try {
            PreparedStatement ps = con.prepareStatement(query1);
            ResultSet rs = ps.executeQuery();
            rs.next();
            BigDecimal income = rs.getBigDecimal(1);

            ps = con.prepareStatement(query2);
            rs = ps.executeQuery();
            rs.next();
            BigDecimal outcome = rs.getBigDecimal(1);

            if(income == null)return new BigDecimal(0).setScale(3);
            if(outcome == null) return income.setScale(3);
            return income.subtract(outcome).setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
