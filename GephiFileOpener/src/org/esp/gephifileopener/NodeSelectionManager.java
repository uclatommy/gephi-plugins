/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esp.gephifileopener;

import java.util.ArrayList;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.openide.util.Lookup;

/**
 *
 * @author Thomas
 */
public class NodeSelectionManager{
    private final SelectionManager sm = VizController.getInstance().getSelectionManager();
    private final ArrayList<Edge> currentSelection = new ArrayList<Edge>();
    private Node currentRootNode;
    private final DirectedGraph dgraph = Lookup.getDefault().lookup(GraphController.class).getModel().getDirectedGraph();
    
    public void setRootNode(Node node)
    {
        currentRootNode = node;
    }
    
    public Node getRootNode()
    {
        return currentRootNode;
    }
    
    
    public Node[] getNeighbors()
    {
        return dgraph.getNeighbors(currentRootNode).toArray();
    }
    
    public void centerOnRoot()
    {
        if(currentRootNode!=null)
        {
            sm.centerOnNode(currentRootNode);
        }
    }
    
    public void setSelection(){
        if(currentSelection!=null)
        {    
            resetSelection();
            for(Edge edge: dgraph.getEdges(currentRootNode))
            {
                currentSelection.add(edge);
            }
            displaySelection();
        }
    }
    
    private void displaySelection()
    {
        if(currentSelection != null)
        {
            for(Edge edge: currentSelection)
            {
                sm.selectEdge(edge);
            }
        }
    }
    
    public void pushDependents()
    {
        ArrayList<Edge> nextSelection = new ArrayList<Edge>();
        ArrayList<Node> selectedNodes = new ArrayList<Node>();
        for(Edge edge: currentSelection) //loop through current selected edges
        {
            selectedNodes.add(edge.getTarget()); //for each edge in selection, add target node to selected nodes array
        }
        for(Node node: selectedNodes) //for each node in selected nodes array, loop though out edges
        {
            for(Edge edge: dgraph.getOutEdges(node))//add each out edge to next selection array
            {
                nextSelection.add(edge);
            }
        }
        for(Edge edge: nextSelection) //for each edge in next selection array,
        {
            currentSelection.add(edge);//add to current selection
        }
        displaySelection();
    }
    
    public void pushPrecedents()
    {
        ArrayList<Edge> nextSelection = new ArrayList<Edge>();
        ArrayList<Node> selectedNodes = new ArrayList<Node>();
        for(Edge edge: currentSelection) //loop through current selected edges
        {
            selectedNodes.add(edge.getSource()); //for each edge in selection, add source node to selected nodes array
        }
        for(Node node: selectedNodes) //for each node in selected nodes array, loop though in edges
        {
            for(Edge edge: dgraph.getInEdges(node))//add each in edge to next selection array
            {
                nextSelection.add(edge);
            }
        }
        for(Edge edge: nextSelection) //for each edge in next selection array,
        {
            currentSelection.add(edge);//add to current selection
        }
        displaySelection();
    }
    
    public void setPairwiseSelection(final Node opposite){
        if(currentRootNode != null || opposite != null)
        {
            resetSelection(); //why doesn't this work?
            Edge selectedEdge = dgraph.getEdge(opposite, currentRootNode);
            if(selectedEdge==null)
            {
                selectedEdge = dgraph.getEdge(currentRootNode, opposite);
            }
            if(selectedEdge!=null)
            {
                currentSelection.add(selectedEdge);
                displaySelection();
            }
        }
    }
    
    public void resetSelection()
    {
        currentSelection.clear();
        sm.resetSelection();
    }
    
    public boolean isDependent(Node opposite)
    {
        return (dgraph.isSuccessor(currentRootNode, opposite));
    }
    
    public boolean isPrecedent(Node opposite)
    {
        return (dgraph.isPredecessor(currentRootNode, opposite));
    }
}
