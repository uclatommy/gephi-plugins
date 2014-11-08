package org.esp.gephifileopener;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thomas
 */
 @ServiceProvider(service = Generator.class)
 public class MosesDataManager implements Generator, LongTask {
 
    protected ProgressTicket progress;
    protected boolean cancel = false;
    
    private FoxproDB mosesModel;
    private String modelDirectory;
 
    @Override
    public void generate(ContainerLoader container) {
        Progress.setDisplayName(progress, "Importing Moses Nodes...");
        Progress.start(progress);
        
        ResultSet mosesNodes = getMosesNodes();
        ResultSet mosesEdges = getMosesEdges();
                     // create nodes
        AttributeColumn cppfile = container.getAttributeModel().getNodeTable().addColumn("cppfile", AttributeType.STRING);
        AttributeColumn prod = container.getAttributeModel().getNodeTable().addColumn("prod", AttributeType.STRING);
        AttributeColumn purp = container.getAttributeModel().getNodeTable().addColumn("purp", AttributeType.STRING);
        AttributeColumn submodel = container.getAttributeModel().getNodeTable().addColumn("submodel", AttributeType.STRING);
        AttributeColumn type = container.getAttributeModel().getNodeTable().addColumn("type", AttributeType.STRING);
        AttributeColumn category = container.getAttributeModel().getNodeTable().addColumn("category", AttributeType.STRING);
        AttributeColumn level = container.getAttributeModel().getNodeTable().addColumn("[z]", AttributeType.INT);
        AttributeColumn subpath = container.getAttributeModel().getNodeTable().addColumn("unique_nm", AttributeType.STRING);
        int levelcount;
        String unique_nm;
        try {
            while(mosesNodes.next())
            {
                NodeDraft n = container.factory().newNodeDraft();
                n.setId(mosesNodes.getString("mId").trim() + mosesNodes.getString("Id").trim());
                //String id = mosesNodes.getString("Id");
                //if(container.getNode(id)==null)
                //{
                //n.setId(id);
                n.setLabel(mosesNodes.getString("Label").trim());
                n.addAttributeValue(cppfile, mosesNodes.getString("cppfile").trim());
                n.addAttributeValue(prod, mosesNodes.getString("prod").trim());
                n.addAttributeValue(purp, mosesNodes.getString("purp").trim());
                n.addAttributeValue(submodel, mosesNodes.getString("submodel").trim());
                n.addAttributeValue(type, mosesNodes.getString("type").trim());
                n.addAttributeValue(category, mosesNodes.getString("category").trim());
                unique_nm = mosesNodes.getString("unique_nm").trim();
                levelcount = unique_nm.length() - unique_nm.replace("|", "").length();
                n.addAttributeValue(level, levelcount);
                n.addAttributeValue(subpath, unique_nm);
                container.addNode(n);
                //}
                //else
                //{
                //    System.out.println(id + "already exists!");
                //}
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            while(mosesEdges.next())
            {
               EdgeDraft e = container.factory().newEdgeDraft();
               e.setSource(container.getNode(mosesEdges.getString("Source").trim()));
               e.setTarget(container.getNode(mosesEdges.getString("Target").trim()));
               container.addEdge(e);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        mosesModel.closeDB();
        Progress.finish(progress);
    }
    
    private ResultSet getMosesEdges()
    {
        FoxproDB selectQry = new FoxproDB(modelDirectory);
        if(mosesModel.isOpen())
        {
            mosesModel.closeDB();
        }
        mosesModel.setLocation(modelDirectory);
        
        Map<String, String> map = new HashMap<String, String>();
        ResultSet rs;
        String strEdges;
        String[] tables = {"CSH", "MTB", "CALCVAR"};
        String prod = "fproduct";
        String purp = "fpurpose";
        String name = "form_name";
        for(String tbl: tables)
        {
            if(tbl.equalsIgnoreCase("CALCVAR"))
            {
                prod = "product";
                purp = "purpose";
                name = "v_name";
            }
            strEdges = "SELECT " + tbl + "." + tbl + "id AS 'From', fml.fmlid AS 'To' FROM \"" + modelDirectory + "\\" + tbl +".DBF\", "
                    + "\"" + modelDirectory + "\\FML.DBF\" WHERE fml.fproduct = " + tbl + "."+prod+" AND fml.fpurpose = " + tbl + "."+purp
                    + " AND fml.form_name = " + tbl + "."+name+" AND fml.fmlid <> " + tbl + "." + tbl + "id";
            rs = selectQry.executeQuery(strEdges);
            
            try {
                while(rs.next())
                {
                    map.put(rs.getString("From"), rs.getString("To"));
                }
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        strEdges = "SELECT fml.fmlid AS id FROM \"" + modelDirectory + "\\FML.DBF\" UNION "
                + "SELECT var.varid AS id FROM \"" + modelDirectory + "\\VAR.DBF\"";
        rs = selectQry.executeQuery(strEdges);
        String cur;
        try {
            while(rs.next())
            {
                cur = rs.getString(1);
                //System.out.println(cur);
                map.put(cur, cur);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        strEdges = "CREATE TABLE \"" + modelDirectory + "\\EDGES.DBF\" ('Id' I, 'Target' C(11), 'Source' C(11), 'Type' C(20))";
        mosesModel.executeUpdate(strEdges);
        strEdges = "SELECT token.tokenid as Id, token.fmlid as Target, token.itemid as Source, nodes.mid as Target_mId, ABS(token.modelid) as Source_mId FROM \"" + modelDirectory + "\\TOKEN.DBF\", \"" + modelDirectory + "\\NODES.DBF\" WHERE token.itemid > 0 AND token.fmlid = nodes.id";
        rs = selectQry.executeQuery(strEdges);
        String target, source, target_mid, source_mid, tokenid;
        
        try {
            while(rs.next())
            {
                target = rs.getString("Target");
                source = rs.getString("Source");
                target_mid = rs.getString("Target_mId");
                source_mid = rs.getString("Source_mId");
                tokenid = rs.getString("Id");
                if("0".equals(source_mid))
                {
                    source_mid = target_mid;
                }
                //if(map.get(source)!=null && map.get(target)!=null)
                {
                    strEdges = "INSERT INTO \"" + modelDirectory + "\\EDGES.DBF\" (Id, Target, Source, Type) VALUES (" +
                            tokenid + ", '" + target_mid + map.get(target) + "', '" + source_mid + map.get(source) + "', \"Directed\")";
                    mosesModel.executeUpdate(strEdges);
                }
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        strEdges = "SELECT * FROM \"" + modelDirectory + "\\EDGES.DBF\"";
        rs = mosesModel.executeQuery(strEdges);
        return rs;
    }
    
    private ResultSet getMosesNodes()
    {
        FoxproDB selectQry = new FoxproDB(modelDirectory);
        if(mosesModel.isOpen())
        {
            mosesModel.closeDB();
        }
        mosesModel.setLocation(modelDirectory);
        String strNodes = "CREATE TABLE \"" + modelDirectory + "\\NODES.DBF\" ('mId' I, 'Id' I, 'Submodel' C(20), 'Label' C(50), 'Prod' C(20), 'Purp' C(20), "
                + "'Type' C(20), 'Category' C(50) NULL, 'cppfile' C(254), 'unique_nm' C(254))";
        mosesModel.executeUpdate(strNodes);
        
        //FML
        strNodes = "SELECT modelset.modelid as mId, fml.fmlid as Id, modelset.sm_name as Submodel, fml.form_name as Label, fml.fproduct as Prod, fml.fpurpose as Purp, catg.category as Category, modelset.unique_nm as unique_nm FROM \"" +
                modelDirectory + "\\FML.DBF\", \"" + modelDirectory + "\\CATG.DBF\", \"" + modelDirectory + "\\MODELSET.DBF\" WHERE fml.cat_id = catg.cat_id AND fml.fproduct = modelset.product AND fml.fpurpose = modelset.purpose UNION " +
                "SELECT modelset.modelid as mId, fml.fmlid as Id, modelset.sm_name as Submodel, fml.form_name as Label, fml.fproduct as Prod, fml.fpurpose as Purp, '' as Category, modelset.unique_nm as unique_nm FROM \"" +
                modelDirectory + "\\FML.DBF\", \"" + modelDirectory + "\\MODELSET.DBF\" WHERE (fml.cat_id NOT IN (SELECT catg.cat_id FROM \"" + modelDirectory + "\\CATG.DBF\")) AND fml.fproduct = modelset.product AND fml.fpurpose = modelset.purpose";
        ResultSet rs = selectQry.executeQuery(strNodes);
        int rowcount = 0;
        try {
            while(rs.next())
            {
                rowcount++;
                strNodes = "INSERT INTO NODES (mId, Id, Submodel, Label, Prod, Purp, Type, Category, cppfile, unique_nm) VALUES (" + rs.getString("mId").trim() + ", " + rs.getString("Id").trim()
                        + ", '" + rs.getString("Submodel").trim() + "', '" + rs.getString("Label").trim() + "', '" + rs.getString("Prod").trim() + "', '" + rs.getString("Purp").trim() 
                        + "', 'FML', '" + rs.getString("Category").trim() + "', '" + modelDirectory + "\\Source\\FML\\" + rs.getString("Prod").trim() 
                        + "_" + rs.getString("Purp").trim() + "_" + rs.getString("Label").trim() + ".cpp', '" + rs.getString("unique_nm") +"')";
        mosesModel.executeUpdate(strNodes);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("FML Query: " + rowcount);
        //VAR
        strNodes = "SELECT modelset.modelid as mId, var.varid as Id, modelset.sm_name as Submodel, var.v_name as Label, var.product as Prod, var.purpose as Purp, catg.category as Category, modelset.unique_nm as unique_nm FROM \"" +
                modelDirectory + "\\VAR.DBF\", \"" + modelDirectory + "\\CATG.DBF\", \"" + modelDirectory + "\\MODELSET.DBF\" WHERE var.cat_id = catg.cat_id AND var.product = modelset.product AND var.purpose = modelset.purpose UNION " +
                "SELECT modelset.modelid as mId, var.varid as Id, modelset.sm_name as Submodel, var.v_name as Label, var.product as Prod, var.purpose as Purp, '' as Category, modelset.unique_nm as unique_nm FROM \"" +
                modelDirectory + "\\VAR.DBF\", \"" + modelDirectory + "\\MODELSET.DBF\" WHERE (var.cat_id NOT IN (SELECT catg.cat_id FROM \"" + modelDirectory + "\\CATG.DBF\")) AND var.product = modelset.product AND var.purpose = modelset.purpose";
        rs = selectQry.executeQuery(strNodes);
        rowcount = 0;
        try {
            while(rs.next())
            {
                rowcount++;
                strNodes = "INSERT INTO NODES (mId, Id, Submodel, Label, Prod, Purp, Type, Category, cppfile, unique_nm) VALUES (" + rs.getString("mId").trim() + ", " + rs.getString("Id").trim()
                        + ", '" + rs.getString("Submodel").trim() + "', '" + rs.getString("Label").trim() + "', '" + rs.getString("Prod").trim() + "', '" + rs.getString("Purp").trim() 
                        + "', 'VAR', '" + rs.getString("Category").trim() + "', '', '"+ rs.getString("unique_nm") +"')";
        mosesModel.executeUpdate(strNodes);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("VAR Query: " + rowcount);
        strNodes = "SELECT * FROM \"" + modelDirectory +"\\NODES.DBF\"";
        rs = selectQry.executeQuery(strNodes);
        return rs;
    }
 
    @Override
    public String getName() {
        return "New Moses Nodes";
    }
 
    @Override
    public GeneratorUI getUI() {
        return null;
    }
 
    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }
 
    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }
    
    public void setMosesModel(String model_directory)
    {
        modelDirectory = model_directory;
        mosesModel = new FoxproDB(model_directory);
    }
 }
