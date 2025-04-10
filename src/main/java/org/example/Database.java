package org.example;



import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    String url;
    String user;
    String password;
    String dbname;
    Connection conn;

    JFrame frame;

    public Database(String url, String user, String password, String dbName,JFrame frame) {
        this.url = url;
        this.password = password;
        this.user = user;
        this.dbname = dbName;
        this.frame=frame;

        try
        {
            this.conn = DriverManager.getConnection(this.url, this.user, this.password);
        }
        catch (SQLException var6)
        {
            JOptionPane.showMessageDialog(frame, "Error: Unable to connect " +var6.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int executeQuery(String sql)
    {
        int result =0;
        try

        {
            Statement st = this.conn.createStatement();
            result = st.executeUpdate(sql);
            st.close();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(frame, "Error: Unable to connect " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error connecting to the database");
            System.out.println(e.getMessage());
        }
        return result;
    }

    public ResultSet getData(String sql) {
        ResultSet rs = null;

        try {
            Statement st = this.conn.createStatement();
            rs = st.executeQuery(sql);
            st.close();
        } catch (Exception var5) {
            System.out.println("Error connecting to the database. ");
            System.out.println(var5.getMessage());
        }

        return rs;
    }

}
