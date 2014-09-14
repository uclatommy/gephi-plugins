/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esp.gephifileopener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.openide.util.Exceptions;

/**
 *
 * @author Thomas
 */
class FoxproDB{
    private String location;
    private String connString;
    private Connection conn;
    private Statement stmt;
    private Boolean open = false;

    FoxproDB(String loc)
    {
        setLocation(loc);
        openDB();
    }

    boolean isOpen()
    {
        return open;
    }

    void openDB()
    {
        try{
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            connString =    "jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=DBF;SourceDB="+location+";\n" +
                            "Exclusive=No;Collate=Machine;NULL=NO;DELETED=NO;BACKGROUNDFETCH=NO;";
            conn = DriverManager.getConnection(connString);
            stmt = conn.createStatement();
            open = true;
        }
        catch (ClassNotFoundException ex) 
        {
            open = false;
            Exceptions.printStackTrace(ex);
        } catch (SQLException ex) {
            open = false;
            Exceptions.printStackTrace(ex);
        }
    }

    void setLocation(String loc)
    {
        location = loc;
    }

    void closeDB()
    {
        try{
            conn.close();
            stmt.close();
            open = false;
        }
        catch (SQLException ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }

    ResultSet executeQuery(String query)
    {
        if(!open)
        {
            openDB();
        }

        ResultSet rs;
        try{
            rs = stmt.executeQuery(query);
        }
        catch (SQLException ex)
        {
            Exceptions.printStackTrace(ex);
            rs = null;
        }
        return rs;
    }

    int executeUpdate(String query)
    {
        if(!open)
        {
            openDB();
        }

        int rs;
        try{
            rs = stmt.executeUpdate(query);
            conn.commit();
        }
        catch (SQLException ex)
        {
            Exceptions.printStackTrace(ex);
            rs = 0;
        }
        return rs;
    }
}
