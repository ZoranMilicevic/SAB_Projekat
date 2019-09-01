package student;

import operations.CityOperations;
import student.DBConnector.mz150346_DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mz150346_City implements CityOperations {

    public static Map<Integer, Integer> getConnectedCitiesWithDist(int cityId){
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select cityId1, cityId2, distance from Line where cityId1 = ? or cityId2 = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, cityId);
            ps.setInt(2, cityId);

            HashMap<Integer, Integer> l = new HashMap<>();
            ResultSet rs = ps.executeQuery();
            while(rs.next() != false){
                int id1 = rs.getInt(1);
                int id2 = rs.getInt(2);
                int distance = rs.getInt(3);
                if(id1 != cityId) l.put(id1, distance);
                else if(id2 != cityId)l.put(id2, distance);
            }
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int createCity(String name) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "insert into City(Name) values (?)";

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);

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
    public List<Integer> getCities() {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from City";
        ArrayList<Integer> l = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement(query);
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

    @Override
    public int connectCities(int cityId1, int cityId2, int distance) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "insert into Line(Distance, CityId1, CityId2) values (?, ?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, distance);
            ps.setInt(2, cityId1);
            ps.setInt(3, cityId2);

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
    public List<Integer> getConnectedCities(int cityId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select cityId1, cityId2 from Line where cityId1 = ? or cityId2 = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, cityId);
            ps.setInt(2, cityId);

            ArrayList<Integer> l = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while(rs.next() != false){
                int id1 = rs.getInt(1);
                int id2 = rs.getInt(2);
                if(id1 != cityId) l.add(id1);
                else if(id2 != cityId)l.add(id2);
            }
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Integer> getShops(int cityId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select id from Shop where cityId = ?";
        ArrayList<Integer> l = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, cityId);
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
