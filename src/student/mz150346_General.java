package student;

import operations.GeneralOperations;
import student.DBConnector.mz150346_DB;
import student.DijkstraGraph.mz150346_Node;
import student.mz150346_Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class mz150346_General implements GeneralOperations {

    public static Calendar currentTime;

    @Override
    public void setInitialTime(Calendar time) {
        currentTime = new GregorianCalendar();
        currentTime.setTime(time.getTime());
    }

    @Override
    public Calendar getCurrentTime() {
        Calendar c= new GregorianCalendar();
        c.setTime(currentTime.getTime());
        return c;
    }

    @Override
    public Calendar time(int days) {
        currentTime.add(Calendar.DAY_OF_YEAR, days);

        Connection con = mz150346_DB.getInstance().getConnection();
        String query0 = "select o.id, o.location, o.expectedAssembleTime, b.cityId, o.nextLocation from orderr o, buyer b where expectedAssembleTime is not null and expectedAssembleTime <= ? and state = 'sent' and o.buyerId = b.id";
        String query1 = "select o.id, o.location, o.expectedArrivalTime, b.cityId, o.nextLocation from orderr o, buyer b where expectedAssembleTime is null and expectedArrivalTime <= ? and state = 'sent' and o.buyerId = b.id";
        String query2 = "update Orderr set location = ?, expectedArrivalTime =?, expectedAssembleTime = null, nextLocation = ? where id = ? ";
        String query3 = "select distance from Line where cityId1 = ? and cityId2 = ? or cityId1 = ? and cityId2 = ?";
        String query4 = "update Orderr set state = 'arrived', recievedTime = expectedArrivalTime, location = nextLocation where id = ?";

        try {
            //assembled just now
            PreparedStatement ps0 = con.prepareStatement(query0);
            ps0.setDate(1, new java.sql.Date(currentTime.getTimeInMillis()));
            ResultSet rs0 = ps0.executeQuery();

            //arrived on the next location on the way to final destination
            PreparedStatement ps = con.prepareStatement(query1);
            ps.setDate(1, new java.sql.Date(currentTime.getTimeInMillis()));
            ResultSet rs = ps.executeQuery();

            List<Integer> listId = new LinkedList<>();
            List<Integer> listLocation = new LinkedList<>();
            List<java.sql.Date> listDate = new LinkedList<>();
            List<Integer> listCity = new LinkedList<>();
            List<Integer> listNextLocation = new LinkedList<>();

            while(rs0.next()!=false){
                listId.add(rs0.getInt(1));
                listLocation.add(rs0.getInt(2));
                listDate.add(rs0.getDate(3));
                listCity.add(rs0.getInt(4));
                listNextLocation.add(rs0.getInt(5));
            }

            while(rs.next()!=false){
                listId.add(rs.getInt(1));
                listLocation.add(rs.getInt(2));
                listDate.add(rs.getDate(3));
                listCity.add(rs.getInt(4));
                listNextLocation.add(rs.getInt(5));
            }

            for(int i=0; i<listId.size(); i++){
                int id = listId.get(i);
                int location = listLocation.get(i);
                java.sql.Date date = listDate.get(i);
                int city = listCity.get(i);
                Integer nextCity = listNextLocation.get(i);

                if(nextCity.equals(new Integer(city))){ // ARRIVED
                    ps = con.prepareStatement(query4);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                else { // NOT ARRIVED
                    mz150346_Order.createGraph();
                    mz150346_Order.graph.calculateShortestPathFromSource(mz150346_Order.graph, mz150346_Order.graph.getNodes().get(city));

                    mz150346_Node n = nextCity == 0 ? mz150346_Order.graph.getNodes().get(location): mz150346_Order.graph.getNodes().get(nextCity);
                    List<mz150346_Node> sp = n.getShortestPath();

                    int nextLocation = sp.get(sp.size() - 1).getId();

                    ps = con.prepareStatement(query3);
                    ps.setInt(1, nextCity == 0 ? location : nextCity);
                    ps.setInt(2, nextLocation);
                    ps.setInt(3, nextLocation);
                    ps.setInt(4, nextCity == 0 ? location : nextCity);
                    rs = ps.executeQuery();
                    rs.next();
                    int distance = rs.getInt(1);

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(date.getTime());
                    c.add(Calendar.DAY_OF_YEAR, distance);
                    date.setTime(c.getTimeInMillis());

                    ps = con.prepareStatement(query2);
                    ps.setInt(1, nextCity == 0 ? location : nextCity);
                    ps.setDate(2, date);
                    ps.setInt(3, nextLocation);
                    ps.setInt(4, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return currentTime;
    }

    @Override
    public void eraseAll() {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "delete from TransactionBuyer where 1=1";
        String query2 = "delete from TransactionSystem where 1=1";
        String query3 = "delete from Line where 1=1";
        String query4 = "delete from OrderItem where 1=1";
        String query5 = "delete from Orderr where 1=1";
        String query6 = "delete from Buyer where 1=1";
        String query7 = "delete from Article where 1=1";
        String query8 = "delete from Shop  where 1=1";
        String query9 = "delete from City where 1=1";


        try {
            PreparedStatement ps1 = con.prepareStatement(query1);
            ps1.execute();

            ps1 = con.prepareStatement(query2);
            ps1.execute();

            ps1 = con.prepareStatement(query3);
            ps1.execute();

            ps1 = con.prepareStatement(query4);
            ps1.execute();

            ps1 = con.prepareStatement(query5);
            ps1.execute();

            ps1 = con.prepareStatement(query6);
            ps1.execute();

            ps1 = con.prepareStatement(query7);
            ps1.execute();

            ps1 = con.prepareStatement(query8);
            ps1.execute();

            ps1 = con.prepareStatement(query9);
            ps1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
