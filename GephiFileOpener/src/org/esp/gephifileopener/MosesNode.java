package org.esp.gephifileopener;


import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thomas
 */
class MosesNode implements Node{
        private final Node node;
        MosesNode(Node inNode){
            node = inNode;
        }
        public Node getNode(){
            return node;
        }
        @Override
        public String toString(){
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            return row.getValue("prod") + "_" + row.getValue("purp") + "->" + node.getNodeData().getLabel();
        }

        @Override
        public int getId() {
            return node.getId();
        }

        @Override
        public NodeData getNodeData() {
            return node.getNodeData();
        }

        @Override
        public Attributes getAttributes() {
            return node.getAttributes();
        }
        
        public String getFilename()
        {
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            return row.getValue("cppfile").toString();
        }
    }