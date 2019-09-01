/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student.DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tasha
 */
public class mz150346_DB {
    private static final String username="sa";
    private static final String password="123";
    private static final String database="mz150346";
    private static final int port=1433;
    private static final String serverName="localhost";

    //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
    //link ka zvanicnom sajtu: https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-2017
    private static final String connectionString="jdbc:sqlserver://"+serverName+":"+port+";"+
            "database="+database+";user="+username+";password="+password;
    //   private static final String connectionString="jdbc:sqlserver://"+serverName+":"+port+";"+
    //         "database="+database+";integratedSecurity=true;

    private Connection connection;
    private mz150346_DB(){
        try {
            connection=DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            Logger.getLogger(mz150346_DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static mz150346_DB db=null;
    public static mz150346_DB getInstance()
    {
        if(db==null)
            db=new mz150346_DB();
        return db;
    }
    public Connection getConnection() {
        return connection;
    }


}

