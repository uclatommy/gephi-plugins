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
            }
            catch (SQLException ex)
            {
                Exceptions.printStackTrace(ex);
                rs = 0;
            }
            return rs;
        }
    }
    foxproDB mosesOutput;
    foxproDB mosesFML;
    
    public boolean mosesOutputReady()
    {
        if(mosesOutput!=null)
        {
            return mosesOutput.isOpen();
        }
        else
        {
            return false;
        }
    }
    
    public boolean mosesFMLReady()
    {
        if(mosesFML!=null)
        {
            return mosesFML.isOpen();
        }
        else
        {
            return false;
        }
    }
    
    public void setModelDirectory(String model_directory)
    {
        modelDirectory = model_directory;
        mosesFML = new foxproDB(modelDirectory);
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
            //mosesOutput.closeDB();
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
            //mosesOutput.closeDB();
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
            //mosesOutput.closeDB();
        } catch (SQLException ex){
            Exceptions.printStackTrace(ex);
            return "";
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
            //mosesOutput.closeDB();
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
        double result = -1.0;
        String submodel = getSubModel(model, node);
        if(!submodel.equals(""))
        {
            String tableName = model + getSubModel(model, node);
            String columnName = getColumnName(model, node);
            if(!columnName.equals(""))
            {
                try
                {
                    String qryString = "SELECT * FROM \"" + tableName.trim() + ".DBF\" WHERE period = '" + period + "'";
                    ResultSet rs = mosesOutput.executeQuery(qryString);
                    rs.next();
                    result = rs.getDouble(columnName);
                    //mosesOutput.closeDB();
                } 
                catch (SQLException ex)
                {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return result;
    }
    
    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i=0; i<len; i+=partitionSize)
        {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }
    
    public void updateFML(Node node, String content)
    {
        AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
        String fmlid = row.getValue("Id").toString();
        String prod = row.getValue("prod").toString();
        String purp = row.getValue("purp").toString();
        try {
            String lines[] = content.split("\\r?\\n");
            String qryUpdate = "";
            int count = 0;
            for(String line: lines)
            {
                line = line.replaceAll("'", "' + CHR(39) + '");
                if(count==0)
                {
                    qryUpdate = "UPDATE \"FML.DBF\" set formula = '" + line +"' WHERE fmlid = " + fmlid;
                }
                else
                {
                    qryUpdate = "UPDATE \"FML.DBF\" set formula = formula + CHR(13) + CHR(10) + '" + line +"' WHERE fmlid = " + fmlid;
                }
                mosesFML.executeUpdate(qryUpdate);
                count++;
            }
            mosesFML.executeUpdate(
                "UPDATE \"CHGD.DBF\" SET flag = .T. WHERE product = \'"+prod+"\' AND purpose = \'"+purp+"\'"
            );
            //mosesFML.closeDB();
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
