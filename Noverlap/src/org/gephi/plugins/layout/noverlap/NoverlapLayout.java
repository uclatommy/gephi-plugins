/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.layout.noverlap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

/**
 *
 * @author Mathieu Jacomy
 */
public class NoverlapLayout extends AbstractLayout implements Layout {

    protected Graph graph;
    private double speed;
    private double ratio;
    private double margin;
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;

    public NoverlapLayout(LayoutBuilder layoutBuilder) {
        super(layoutBuilder);
    }

    public void initAlgo() {
        this.graph = graphModel.getGraphVisible();
        setConverged(false);
    }

    public void goAlgo() {
        setConverged(true);
        this.graph = graphModel.getGraphVisible();
        Node[] nodes = graph.getNodes().toArray();

        //Reset Layout Data
        for (Node n : nodes) {
            if (n.getNodeData().getLayoutData() == null || !(n.getNodeData().getLayoutData() instanceof NoverlapLayoutData)) {
                n.getNodeData().setLayoutData(new NoverlapLayoutData());
            }
            NoverlapLayoutData layoutData = n.getNodeData().getLayoutData();
            layoutData.neighbours.clear();
            layoutData.dx = 0;
            layoutData.dy = 0;
        }

        // Get xmin, xmax, ymin, ymax
        this.xmin = Double.MAX_VALUE;
        this.xmax = Double.MIN_VALUE;
        this.ymin = Double.MAX_VALUE;
        this.ymax = Double.MIN_VALUE;

        for (Node n : nodes) {
            float x = n.getNodeData().x();
            float y = n.getNodeData().y();
            float radius = n.getNodeData().getRadius();

            // Get the rectangle occupied by the node
            double nxmin = x - (radius * ratio + margin);
            double nxmax = x + (radius * ratio + margin);
            double nymin = y - (radius * ratio + margin);
            double nymax = y + (radius * ratio + margin);

            // Update global boundaries
            this.xmin = Math.min(this.xmin, nxmin);
            this.xmax = Math.max(this.xmax, nxmax);
            this.ymin = Math.min(this.ymin, nymin);
            this.ymax = Math.max(this.ymax, nymax);
        }

        // Secure the bounds
        double xwidth = this.xmax - this.xmin;
        double yheight = this.ymax - this.ymin;
        double xcenter = (this.xmin + this.xmax) / 2;
        double ycenter = (this.ymin + this.ymax) / 2;
        double securityRatio = 1.1;
        this.xmin = xcenter - securityRatio * xwidth / 2;
        this.xmax = xcenter + securityRatio * xwidth / 2;
        this.ymin = ycenter - securityRatio * yheight / 2;
        this.ymax = ycenter + securityRatio * yheight / 2;

        SpatialGrid grid = new SpatialGrid();

        // Put nodes in their boxes
        for (Node n : nodes) {
            grid.add(n);
        }

        // Now we have cells with nodes in it. Nodes that are in the same cell, or in adjacent cells, are tested for repulsion.
        // But they are not repulsed several times, even if they are in several cells...
        // So we build a relation of proximity between nodes.

        // Build proximities
        for (int row = 0; row < grid.countRows(); row++) {
            for (int col = 0; col < grid.countColumns(); col++) {
                for (Node n : grid.getContent(row, col)) {
                    NoverlapLayoutData lald = n.getNodeData().getLayoutData();

                    // For node n in the box "box"...
                    // We search nodes that are in the boxes that are adjacent or the same.
                    for (int row2 = Math.max(0, row - 1); row2 <= Math.min(row + 1, grid.countRows() - 1); row2++) {
                        for (int col2 = Math.max(0, col - 1); col2 <= Math.min(col + 1, grid.countColumns() - 1); col2++) {
                            for (Node n2 : grid.getContent(row2, col2)) {
                                if (n2 != n && !lald.neighbours.contains(n2)) {
                                    lald.neighbours.add(n2);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Proximities are built !

        // Apply repulsion force - along proximities...
        for (Node n1 : nodes) {
            NoverlapLayoutData lald = n1.getNodeData().getLayoutData();
            for (Node n2 : lald.neighbours) {
                float n1x = n1.getNodeData().x();
                float n1y = n1.getNodeData().y();
                float n2x = n2.getNodeData().x();
                float n2y = n2.getNodeData().y();
                float n1radius = n1.getNodeData().getRadius();
                float n2radius = n2.getNodeData().getRadius();

                // Check sizes (spheric)
                double xDist = n2x - n1x;
                double yDist = n2y - n1y;
                double dist = Math.sqrt(xDist * xDist + yDist * yDist);
                boolean collision = dist < (n1radius * ratio + margin) + (n2radius * ratio + margin);
                if (collision) {
                    setConverged(false);
                    // n1 repulses n2, as strongly as it is big
                    NoverlapLayoutData layoutData = n2.getNodeData().getLayoutData();
                    double f = 1. + n1.getNodeData().getSize();
                    if(dist>0){
                        layoutData.dx += xDist / dist * f;
                        layoutData.dy += yDist / dist * f;
                    } else {
                        // Same exact position, divide by zero impossible: jitter
                        layoutData.dx += 0.01 * (0.5 - Math.random());
                        layoutData.dy += 0.01 * (0.5 - Math.random());
                    }
                }

            }
        }

        // apply forces
        for (Node n : nodes) {
            NoverlapLayoutData layoutData = n.getNodeData().getLayoutData();
            if (!n.getNodeData().isFixed()) {
                layoutData.dx *= 0.1 * speed;
                layoutData.dy *= 0.1 * speed;
                float x = n.getNodeData().x() + layoutData.dx;
                float y = n.getNodeData().y() + layoutData.dy;

                n.getNodeData().setX(x);
                n.getNodeData().setY(y);
            }
        }
    }

    public void endAlgo() {
        for (Node n : graph.getNodes()) {
            n.getNodeData().setLayoutData(null);
        }
    }

    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        final String NOVERLAP_CATEGORY = "Noverlap";
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, "speed", NOVERLAP_CATEGORY, "speed", "getSpeed", "setSpeed"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, "ratio", NOVERLAP_CATEGORY, "ratio", "getRatio", "setRatio"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, "margin", NOVERLAP_CATEGORY, "margin", "getMargin", "setMargin"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    public void resetPropertiesValues() {
        setSpeed(3.);
        setRatio(1.2);
        setMargin(5.);
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    private class SpatialGrid {

        //Param
        private final int COLUMNS_ROWS = 20;
        //Data
        private Map<Cell, List<Node>> data = new HashMap<Cell, List<Node>>();

        public SpatialGrid() {
            for (int row = 0; row < COLUMNS_ROWS; row++) {
                for (int col = 0; col < COLUMNS_ROWS; col++) {
                    List<Node> localnodes = new ArrayList<Node>();
                    data.put(new Cell(row, col), localnodes);
                }
            }
        }

        public Iterable<Node> getContent(int row, int col) {
            return data.get(new Cell(row, col));
        }

        public int countColumns() {
            return COLUMNS_ROWS;
        }

        public int countRows() {
            return COLUMNS_ROWS;
        }

        public void add(Node node) {
            float x = node.getNodeData().x();
            float y = node.getNodeData().y();
            float radius = node.getNodeData().getRadius();

            // Get the rectangle occupied by the node
            double nxmin = x - (radius * ratio + margin);
            double nxmax = x + (radius * ratio + margin);
            double nymin = y - (radius * ratio + margin);
            double nymax = y + (radius * ratio + margin);

            // Get the rectangle as boxes
            int minXbox = (int) Math.floor((COLUMNS_ROWS - 1) * (nxmin - xmin) / (xmax - xmin));
            int maxXbox = (int) Math.floor((COLUMNS_ROWS - 1) * (nxmax - xmin) / (xmax - xmin));
            int minYbox = (int) Math.floor((COLUMNS_ROWS - 1) * (nymin - ymin) / (ymax - ymin));
            int maxYbox = (int) Math.floor((COLUMNS_ROWS - 1) * (nymax - ymin) / (ymax - ymin));
            for (int col = minXbox; col <= maxXbox; col++) {
                for (int row = minYbox; row <= maxYbox; row++) {
                    try {
                        data.get(new Cell(row, col)).add(node);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        if (nxmin < xmin || nxmax > xmax) {
                            System.err.println("Xerr0r* - " + node.getId() + " - nxmin=" + nxmin + " this.xmin=" + xmin + " nxmax=" + nxmax + " this.xmax=" + xmax);
                        }
                        if (nymin < ymin || nymax > ymax) {
                            System.err.println("Yerr0r* - " + node.getId() + " - nymin=" + nymin + " this.ymin=" + ymin + " nymax=" + nymax + " this.ymax=" + ymax);
                        }
                    }
                }
            }
        }
    }

    private static class Cell {

        private final int row;
        private final int col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Cell other = (Cell) obj;
            if (this.row != other.row) {
                return false;
            }
            if (this.col != other.col) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 11 * hash + this.row;
            hash = 11 * hash + this.col;
            return hash;
        }
    }
}
