/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esp.gephifileopener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author thchen
 */
public class MosesController {
    private String modelDirectory;
    private String outputDirectory;
    private final List<List<String>> outputLookup = new ArrayList<List<String>>();
    class foxproDB{
        private String location;
        private String connString;
        private Connection conn;
        private Statement stmt;
        private Boolean open = false;
        
        private foxproDB(String loc)
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
            catch (Exception ex) 
            {
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
            catch (Exception ex)
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
            catch (Exception ex)
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
            }
            catch (Exception ex)
            {
                Exceptions.printStackTrace(ex);
                rs = 0;
            }
            return rs;
        }
    }
    foxproDB mosesOutput;
    
    public MosesController(String model_directory)
    {
        modelDirectory = model_directory;
    }
    
    public boolean mosesOutputReady()
    {
        return mosesOutput.isOpen();
    }
    
    public void setModelDirectory(String model_directory)
    {
        modelDirectory = model_directory;
    }
    
    public void setOutputDirectory(String output)
    {
        outputDirectory = output;
        mosesOutput = new foxproDB(outputDirectory);
    }
    
    public ArrayList<String> ms_ListModels(String PathNameBase)
    {
        File folder = new File(PathNameBase);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> result = new ArrayList<String>();
        String pattern = "(.*)\\$[Ii][Nn][Ff][Oo]\\.[Dd][Bb][Ff]";
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().matches(pattern)) {
                    String[] s = listOfFile.getName().split("\\$");
                    result.add(s[0].trim());
                }
            } else if (listOfFile.isDirectory()) {
                System.out.println("Directory " + listOfFile.getName());
            }
        }
        return result;
    }
    
    public ArrayList<String> ms_ListGroups(String PathNameBase, String Model)
    {
        ArrayList<String> result = new ArrayList<String>();
        try{
            ResultSet rs = mosesOutput.executeQuery("SELECT name, product, purpose FROM \"" + Model + "$INFO.DBF\" WHERE RECTYPE = 3");
            while (rs.next())
            {
                result.add(rs.getString("name"));
            }
            mosesOutput.closeDB();
        }
        catch (SQLException ex)
        {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }
    
    public double ms_CashFlow(String PathNameBase, String Model, String Group, String Column, int Period)
    {
        try{
            ResultSet rs = mosesOutput.executeQuery("SELECT "+ Column +" FROM \"" + Model + ".DBF\" WHERE period = " + Period            );
            rs.next();
            double result = rs.getDouble(Column);
            mosesOutput.closeDB();
            return result;
        } catch (SQLException ex){
            Exceptions.printStackTrace(ex);
        }
        return 0.0;
    }
    
    public String getSubModel(String model, Node node)
    {
        String prod = (String)node.getNodeData().getAttributes().getValue("prod");
        String purp = (String)node.getNodeData().getAttributes().getValue("purp");
        String groupMemo = "";
        try{
            ResultSet rs = mosesOutput.executeQuery(
                    "SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + 
                    prod + "' AND purpose = '" + purp + "' AND rectype = 3"
            );
            rs.next();
            groupMemo = rs.getString("name");
            mosesOutput.closeDB();
        } catch (SQLException ex){
            Exceptions.printStackTrace(ex);
        }
        if(!groupMemo.equals(""))
        {
            String[] memosplit = groupMemo.split(",");
            String result = memosplit[0].replaceAll("\"", "");
            result = result.replaceAll("\\|", "~");
            return "~" + result;
        }
        return "";
    }
    
    public String getColumnName(String model, Node node)
    {
        String prod = (String)node.getNodeData().getAttributes().getValue("prod");
        String purp = (String)node.getNodeData().getAttributes().getValue("purp");
        String colName = node.getNodeData().getLabel();
        String colMemo = "";
        try{
            ResultSet rs = mosesOutput.executeQuery(
                    "SELECT memo1 FROM \"" + model + "$INFO.DBF\" WHERE product = '" + 
                    prod + "' AND purpose = '" + purp + "' AND rectype = 2"
            );
            rs.next();
            colMemo = rs.getString("memo1");
            mosesOutput.closeDB();
            System.out.println(colMemo);
        } catch (SQLException ex){
            Exceptions.printStackTrace(ex);
        }
        if(!colMemo.equals(""))
        {
            String result = "";
            String[] records = colMemo.split("\\r?\\n");
            outerloop:
            for(String r: records)
            {
                String[] fields = r.split(",");
                if(fields[0].equals("\""+colName+"\""))
                {
                    result = fields[1].replaceAll("\"","");
                    break;
                }
            }
            return result;
        }
        return "";
    }
    
    public double getOutput(String model, Node node, int period)
    {
        String tableName = model + getSubModel(model, node);
        String columnName = getColumnName(model, node);
        if(columnName.equals("")) return -1.0;
        double result = -1.0;
        try{
            String qryString = "SELECT * FROM \"" + tableName.trim() + ".DBF\" WHERE period = '" + period + "'";
            ResultSet rs = mosesOutput.executeQuery(qryString);
            rs.next();
            result = rs.getDouble(columnName);
            mosesOutput.closeDB();
        } catch (SQLException ex){
            Exceptions.printStackTrace(ex);
        }
        return result;
    }
    
    public void updateFML(Node node, String content)
    {
        AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
        String fmlid = row.getValue("Id").toString();
        String prod = row.getValue("prod").toString();
        String purp = row.getValue("purp").toString();
        try {
            /* Don't know why it wont find chgd.dbf even though it's there!
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM \"CHGD.DBF\""
            );
            for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                System.out.print(rs.getMetaData().getColumnName(j) + "\t");
              }
              System.out.println();

              while (rs.next()) {
                for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                  System.out.print(rs.getObject(j) + "\t");
                }
                System.out.println();
              }
            */
            /*
            stmt.executeUpdate(
                "UPDATE \"CHGD.DBF\" SET flag = .T. WHERE product = \'"+prod+"\' AND purpose = \'"+purp+"\'"
            );
            */
            mosesOutput.executeUpdate(
                "UPDATE \"FML.DBF\" set formula = \'" + content +"\' WHERE fmlid = " + fmlid
            );
            mosesOutput.closeDB();
        } 
        catch (Exception ex) 
        {
            Exceptions.printStackTrace(ex);
        }
    }
    public void updateFML(Node node){
        PrintWriter newfml = null;
        try {
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            String filename = row.getValue("cppfile").toString();
            String filePath = Paths.get(filename).getParent().getParent().toString() + "\\";
            newfml = new PrintWriter(new BufferedWriter(new FileWriter(filePath+"newFML.csv", true)));
            newfml.println(row.getValue("Id")+","+filename);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            newfml.close();
        }
    }
}
