package student;

import operations.ArticleOperations;
import student.DBConnector.mz150346_DB;

import java.sql.*;

public class mz150346_Article implements ArticleOperations {
    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        Connection con = mz150346_DB.getInstance().getConnection();
        String query = "insert into Article(Price, Amount, Name, ShopId) values(?, ?, ?, ?)";
        ResultSet rs = null;

        try {
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, articlePrice);
            ps.setInt(2, 0);
            ps.setString(3, articleName);
            ps.setInt(4, shopId);

            ps.execute();
            rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
