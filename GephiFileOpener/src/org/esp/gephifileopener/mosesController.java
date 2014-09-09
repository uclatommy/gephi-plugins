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
    private String outputFile;
    private List<List<String>> outputLookup = new ArrayList<List<String>>();
    final private String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
    
    public MosesController(String model_directory)
    {
        modelDirectory = model_directory;
    }
    
    public void setModelDirectory(String model_directory)
    {
        modelDirectory = model_directory;
    }
    
    public void setOutputDirectory(String output)
    {
        outputDirectory = output;
    }
    
    private String getConnString(){
        return "jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=DBF;SourceDB="+outputDirectory+";\n" +
            "Exclusive=No;Collate=Machine;NULL=NO;DELETED=NO;BACKGROUNDFETCH=NO;";
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
        try {
            Class.forName(driver);
            String connString = getConnString();
            Connection conn = DriverManager.getConnection(connString);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT name, product, purpose FROM \"" + Model + "$INFO.DBF\" WHERE RECTYPE = 3"
            );
            while (rs.next())
            {
                result.add(rs.getString("name"));
            }
            stmt.close();
            conn.close();
            return result;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public double ms_CashFlow(String PathNameBase, String Model, String Group, String Column, int Period)
    {
        try{
            Class.forName(driver);
            String connString = getConnString();
            Connection conn = DriverManager.getConnection(connString);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT "+ Column +" FROM \"" + Model + ".DBF\" WHERE period = " + Period
            );
            rs.next();
            double result = rs.getDouble(Column);
            stmt.close();
            conn.close();
            return result;
        } catch (Exception ex){
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
            Class.forName(driver);
            String connString = getConnString();
            Connection conn = DriverManager.getConnection(connString);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 3"
            );
            rs.next();
            groupMemo = rs.getString("name");
            stmt.close();
            conn.close();
        } catch (Exception ex){
            Exceptions.printStackTrace(ex);
        }
        if(!groupMemo.equals(""))
        {
            String[] memosplit = groupMemo.split(",");
            //System.out.println(memosplit[0]);
            String result = memosplit[0].replaceAll("\"", "");
            result = result.replaceAll("\\|", "~");
            //System.out.println(result);
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
            //Class.forName(stels_driver);
            Class.forName(driver);
            //Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+outputDirectory);
            //String connString="jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=DBF;SourceDB="+outputDirectory+";\n" +
            //    "Exclusive=No;Collate=Machine;NULL=NO;DELETED=NO;BACKGROUNDFETCH=NO;";//DeafultDir indicates the location of the db
            //String connString="jdbc:odbc:dbf;DataDirectory="+outputDirectory+";";
            String connString = getConnString();
            Connection conn=DriverManager.getConnection(connString);
            Statement stmt = conn.createStatement();
            //System.out.println("SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 3");
            ResultSet rs = stmt.executeQuery(
                    "SELECT memo1 FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 2"
        //            "SELECT * FROM \"GPF_FY13_ID14_0_TESTTC~MAIN\""
            );
            /*
            while(rs.next())
            {
                System.out.println(rs.getString("memo2"));
            }
            */
            rs.next();
            colMemo = rs.getString("memo1");
            stmt.close();
            conn.close();
            //colMemo = rs.getString("group");
            System.out.println(colMemo);
        } catch (Exception ex){
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
                    break outerloop;
                }
            }
            //System.out.println(result);
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
            Class.forName(driver);
            String connString = getConnString();            
            Connection conn = DriverManager.getConnection(connString);
            Statement stmt = conn.createStatement();
            String qryString = "SELECT * FROM \"" + tableName.trim() + ".DBF\" WHERE period = '" + period + "'";
            ResultSet rs = stmt.executeQuery(qryString);
            rs.next();
            result = rs.getDouble(columnName);
            stmt.close();
            conn.close();
        } catch (Exception ex){
            Exceptions.printStackTrace(ex);
        }
        return result;
    }
    
    public void updateFML(Node node, String content)
    {
        try {
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            String fmlid = row.getValue("Id").toString();
            String prod = row.getValue("prod").toString();
            String purp = row.getValue("purp").toString();
            Class.forName(driver);
            String connString = getConnString();
            Connection conn = DriverManager.getConnection(connString);
            Statement stmt = conn.createStatement();
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
            stmt.executeUpdate(
                "UPDATE \"FML.DBF\" set formula = \'" + content +"\' WHERE fmlid = " + fmlid
            );
            
            //rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
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
