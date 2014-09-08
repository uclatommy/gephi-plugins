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
    final private String stels_driver = "jstels.jdbc.dbf.DBFDriver2";
    
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
            Class.forName(stels_driver);
            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+PathNameBase);
            Statement stmt = conn.createStatement();
            //System.out.println("SELECT name, product, purpose FROM \"" + Model + "$INFO.DBF\" WHERE RECTYPE = 3");
            ResultSet rs = stmt.executeQuery(
                    "SELECT name, product, purpose FROM \"" + Model + "$INFO.DBF\" WHERE RECTYPE = 3"
            );
            while (rs.next())
            {
                result.add(rs.getString("name"));
            }
            return result;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public double ms_CashFlow(String PathNameBase, String Model, String Group, String Column, int Period)
    {
        try{
            Class.forName(stels_driver);
            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+PathNameBase);
            Statement stmt = conn.createStatement();
            System.out.println("SELECT "+ Column +" FROM \"" + Model + ".DBF\" WHERE period = " + Period);
            ResultSet rs = stmt.executeQuery(
                    "SELECT "+ Column +" FROM \"" + Model + ".DBF\" WHERE period = " + Period
            );
            while(rs.next())
            {
                System.out.println(rs.getString(Column));
            }
            rs.first();
            return rs.getDouble(Column);
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
            Class.forName(stels_driver);
            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+outputDirectory);
            Statement stmt = conn.createStatement();
            //System.out.println("SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 3");
            ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 3"
            );
            /*
            while(rs.next())
            {
                System.out.println(rs.getString("memo2"));
            }
            */
            rs.first();
            groupMemo = rs.getString("name");
            //System.out.println(groupMemo);
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
            Class.forName(stels_driver);
            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+outputDirectory);
            Statement stmt = conn.createStatement();
            //System.out.println("SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 3");
            ResultSet rs = stmt.executeQuery(
                    "SELECT memo1 FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 2"
            );
            /*
            while(rs.next())
            {
                System.out.println(rs.getString("memo2"));
            }
            */
            rs.first();
            colMemo = rs.getString("memo1");
            //System.out.println(colMemo);
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
            Class.forName(stels_driver);
            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+outputDirectory);
            Statement stmt = conn.createStatement();
            //System.out.println("SELECT name FROM \"" + model + "$INFO.DBF\" WHERE product = '" + prod + "' AND purpose = '" + purp + "' AND rectype = 3");
            ResultSet rs = stmt.executeQuery(
                    "SELECT "+columnName+" FROM \"" + tableName + ".DBF\" WHERE period = " + period
            );
            rs.first();
            result = rs.getDouble(columnName);
            //System.out.println(colMemo);
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
            //System.out.println("modelDirectory is: " +modelDirectory);
            Class.forName(stels_driver);
            Connection conn = DriverManager.getConnection("jdbc:jstels:dbf:"+modelDirectory);
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
