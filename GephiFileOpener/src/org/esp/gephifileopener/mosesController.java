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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
