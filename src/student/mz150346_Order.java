package student;

import javafx.util.Pair;
import operations.OrderOperations;
import student.DBConnector.mz150346_DB;
import student.DijkstraGraph.mz150346_Graph;
import student.DijkstraGraph.mz150346_Node;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class mz150346_Order implements OrderOperations {

    public static mz150346_Graph graph=null;

    public static void createGraph(){
        mz150346_City c = new mz150346_City();
        List<Integer> list = c.getCities();
        graph = new mz150346_Graph();

        for(int cityId:list){
            mz150346_Node n = new mz150346_Node(cityId);
            graph.addNode(n);
        }

        for(int cityId:list){
            Map<Integer, Integer> m = mz150346_City.getConnectedCitiesWithDist(cityId);
            for(Map.Entry<Integer, Integer> e:m.entrySet()){
                graph.getNodes().get(cityId).addAdjecent(e.getKey(), e.getValue());
            }
        }

    }

    public Pair<Integer, Integer> findNearestShop(int buyerId){
        mz150346_Buyer b = new mz150346_Buyer();
        int cityId = b.getCity(buyerId);

        createGraph();
        graph.calculateShortestPathFromSource(graph, graph.getNodes().get(cityId));

        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select distinct(cityId) from Shop";
        List<Integer> listOfCitiesWithShops = new LinkedList<>();

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()!=false){
                listOfCitiesWithShops.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(listOfCitiesWithShops.contains(cityId))return new Pair<>(cityId, 0);

        //find the nearest shop to the buyer city and distance between
        Pair <Integer, Integer> best = new Pair<>(cityId, Integer.MAX_VALUE);
        for(int city:listOfCitiesWithShops){
            int distance = graph.getNodes().get(city).getDistance();
            if(distance < best.getValue()){
                best = new Pair<>(city, distance);
            }
        }

        return best;
    }

    public int findExpectedAssembleTime(int orderId, int cityId){
        createGraph();
        graph.calculateShortestPathFromSource(graph, graph.getNodes().get(cityId));

        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select distinct(cityId) from Shop s, OrderItem oi, Article a where oi.orderId = ? and oi.articleId = a.id and a.shopId = s.id";
        List<Integer> listOfCitiesWithOrderedArtcles = new LinkedList<>();

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next() != false) {
                listOfCitiesWithOrderedArtcles.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //find the max distance between the nearest shop to buyer and all other shops from which he ordered
        int maxDist = 0;
        for(int city:listOfCitiesWithOrderedArtcles){
            int distance = graph.getNodes().get(city).getDistance();
            if(distance>maxDist)maxDist=distance;
        }

        return maxDist;
    }

    @Override
    public int addArticle(int orderId, int articleId, int count) {
        Connection con = mz150346_DB.getInstance().getConnection();
        mz150346_Shop shop = new mz150346_Shop();
        String query1 = "select id, count from OrderItem where orderId = ? and articleId = ?";
        String query2 = "insert into OrderItem(OrderId, ArticleId, Count) values(?, ?, ?)";
        String query3 = "update OrderItem set count = ? where id = ?";

        try {
            String state = getState(orderId);
            if("created".equals(state)) {
                int amountPresent = shop.getArticleCount(articleId);
                int returnVal = -1;

                if (amountPresent >= count) {
                    PreparedStatement ps = con.prepareStatement(query1);
                    ps.setInt(1, orderId);
                    ps.setInt(2, articleId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next() == false) {   //Item is not ordered
                        ps = con.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, orderId);
                        ps.setInt(2, articleId);
                        ps.setInt(3, count);

                        ps.execute();
                        ResultSet rs3 = ps.getGeneratedKeys();
                        rs3.next();

                        returnVal= rs3.getInt(1);
                    } else {                    //item is already ordered, increase count only
                        int id = rs.getInt(1);
                        int presentCount = rs.getInt(2);
                        count = count + presentCount;

                        ps = con.prepareStatement(query3);
                        ps.setInt(1, count);
                        ps.setInt(2, id);
                        ps.executeUpdate();

                        returnVal = id;
                    }

                    shop.increaseArticleCount(articleId, -count);
                    return returnVal;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int completeOrder(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query2 = "update Orderr set state = ?, sentTime = ?, expectedAssembleTime = ?, expectedArrivalTime = ?, location = ? where id = ?";
        String query3 = "insert into Transactionn default values";
        String query4 = "insert into TransactionBuyer(id, orderId, amount) values (?, ?, ?)";

        String query5 = "select articleId, count from OrderItem where id = ?";
        String query6 = "delete from OrderItem where id = ?";
        String query7 = "delete from Orderr where id = ?";

        mz150346_Buyer buyer = new mz150346_Buyer();

        BigDecimal finalPrice = getFinalPrice(orderId);

        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            int buyerId = getBuyer(orderId);
            BigDecimal buyerCredit = buyer.getCredit(buyerId);

            if(finalPrice.compareTo(buyerCredit) == -1){ //finalPrice < buyerCredit, OK
                //set the state, sentDate, expectedArrivalTime, expectedAssembleTime, Location in order
                Pair<Integer, Integer> pp = findNearestShop(buyerId);
                int toBuyerDist = pp.getValue();
                int toAsembleDist = findExpectedAssembleTime(orderId, pp.getKey());
                Calendar c = new GregorianCalendar();
                c.setTime(mz150346_General.currentTime.getTime());

                c.add(Calendar.DAY_OF_YEAR, toAsembleDist);
                ps = con.prepareStatement(query2);
                ps.setString(1, "sent");
                ps.setDate(2, new java.sql.Date(mz150346_General.currentTime.getTimeInMillis()));
                ps.setDate(3, new java.sql.Date(c.getTimeInMillis()));
                c.add(Calendar.DAY_OF_YEAR, toBuyerDist);
                ps.setDate(4, new java.sql.Date(c.getTimeInMillis()));
                ps.setInt(5, pp.getKey());
                ps.setInt(6, orderId);

                ps.executeUpdate();

                //insert into transaction
                ps = con.prepareStatement(query3, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.execute();
                rs = ps.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);

                //insert into transactionBuyer
                ps = con.prepareStatement(query4);
                ps.setInt(1, id);
                ps.setInt(2, orderId);
                ps.setBigDecimal(3, finalPrice);
                ps.execute();

                //update credit of buyer
                buyer.increaseCredit(buyerId, finalPrice.subtract(finalPrice).subtract(finalPrice));

                return 1;
            }
            else{ // finalPrice > buyerCredit, NOT OK
                List<Integer> l = getItems(orderId);
                mz150346_Shop shop = new mz150346_Shop();
                ps = con.prepareStatement(query5);
                PreparedStatement ps1 = con.prepareStatement(query6);
                for(int i:l){
                    //find articleId and count that should be returned from OrderItem
                    ps.setInt(1, i);
                    rs = ps.executeQuery();
                    rs.next();
                    int articleId = rs.getInt(1);
                    int count = rs.getInt(2);

                    //return taken
                    shop.increaseArticleCount(articleId, count);

                    //delete orderItem
                    ps1.setInt(1, i);
                    ps1.execute();
                }
                //delete order
                ps = con.prepareStatement(query7);
                ps.setInt(1, orderId);
                ps.execute();
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public BigDecimal getFinalPrice(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "{ call SP_FINAL_PRICE (?) }";
        String query2 = "select finalPrice from Orderr where id = ?";

        try {
            CallableStatement cs = con.prepareCall(query);
            cs.setInt(1, orderId);
            cs.execute();

            PreparedStatement ps = con.prepareStatement(query2);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getBigDecimal(1).setScale(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getDiscountSum(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query1 = "select sum(oi.count*a.price) from OrderItem oi, Article a where oi.orderId = ? and a.id = oi.articleId";
        String query2 = "select finalPrice from Orderr where id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query1);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            BigDecimal priceWithoutDiscount = rs.getBigDecimal(1);

            ps = con.prepareStatement(query2);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            rs.next();
            BigDecimal priceWithDiscount = rs.getBigDecimal(1);

            return priceWithoutDiscount.subtract(priceWithDiscount);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int removeArticle(int orderId, int articleId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "delete from OrderItem where orderId = ? and articleId = ?";
        String query2 = "select count from OrderItem where orderId = ? and articleId = ?";
        try {
            PreparedStatement ps0 = con.prepareStatement(query2);
            ps0.setInt(1, orderId);
            ps0.setInt(2, articleId);
            ResultSet rs = ps0.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            mz150346_Shop s = new mz150346_Shop();
            s.increaseArticleCount(articleId, count);

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            ps.setInt(2, articleId);
            ps.execute();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Integer> getItems(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select articleId from OrderItem where orderId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> arr = new ArrayList<>();

            while(rs.next() != false){
                arr.add(rs.getInt(1));
            }
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getState(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select o.state from orderr o where id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Calendar getSentTime(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select sentTime from orderr where id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();
            rs.next();
            java.sql.Date d = rs.getDate(1);
            if (d==null)return null;
            Calendar cal = new GregorianCalendar();
            cal.setTime(d);
            return cal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Calendar getRecievedTime(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select RecievedTime from orderr where id = ?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();
            rs.next();
            java.sql.Date d = rs.getDate(1);
            if(d==null)return null;
            Calendar cal = new GregorianCalendar();
            cal.setTime(d);
            return cal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getBuyer(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select buyerId from orderr where id = ?";

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
    public int getLocation(int orderId) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "select location from Orderr where id = ?";

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
}
