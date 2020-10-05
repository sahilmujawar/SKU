/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sku;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author SAHIL
 */
public class Query {
    Connection conn = null;
    
    /*
    Function "dbConnect" is used to initiate connection to Database and also used to restore connection during
    connection closed or connection failure.
    */
    public void dbConnect() throws ClassNotFoundException, SQLException {
        if(conn == null || conn.isClosed()) {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sahil?autoReconnect=true&useSSL=false","root","sahil@2492");
        }
    }
    
    /*
    Function "fetchQueryDetails" is used to execute select query to fetch details from Database table.
    Input to this function is the query string passed from the calling portion of the function and returns
    ResultSet as the return value.
    */
    public ResultSet fetchQueryDetails(String sql) throws ClassNotFoundException, SQLException {
            dbConnect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            return rs;        
    }    
    
    /*
    Function "executeInsert" is used to execute insert into Database table.
    Input to this function is the query string passed from the calling portion.
    */
    public void executeInsert(String sql) throws ClassNotFoundException, SQLException {
            dbConnect();
            Statement statement = conn.createStatement();
            int result = statement.executeUpdate(sql);
    }    
    
    /*
    Function "dbConnectClose" is used to close active Database connections if any.
    It also force closed the Database connecion in case of program error.
    */
    public void dbConnectClose() {
        try {
        if(conn != null && !conn.isClosed()) {
            conn.close();
        }
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }    
}