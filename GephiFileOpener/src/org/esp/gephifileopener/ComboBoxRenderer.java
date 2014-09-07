/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esp.gephifileopener;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.esp.gephifileopener.PanelsTopComponent.NodeListWrapper;

/**
 *
 * @author Thomas
 */
class ComboBoxRenderer extends JPanel implements ListCellRenderer
{

    private static final long serialVersionUID = -1L;
    private ArrayList<NodeListWrapper> nodes;
    private ArrayList<Integer> styles;

    JPanel textPanel;
    JLabel text;
    
    public ComboBoxRenderer(JList list) {
        text = new JLabel();
        text.setOpaque(true);
        text.setFont(list.getFont());
    }
    
    public void setNodes(ArrayList<NodeListWrapper> nod)
    {
        nodes = nod;
    }
    
    public void setStyle(ArrayList<Integer> s)
    {
        styles = s;
    }
    
    public ArrayList<NodeListWrapper> getNodes()
    {
        return nodes;
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (nodes == null)
        {
            System.out.println("use setNodes first.");
            return this;
        }
        if(styles==null)
        {
            System.out.println("use setStyles first.");
            return this;
        }
        if (index>-1) {
            NodeListWrapper cur = nodes.get(index);
            Color nodeColor = new Color(cur.getNodeData().r(),cur.getNodeData().g(),cur.getNodeData().b());
            if (isSelected)
            {
                setBackground(mixColor(Color.WHITE,nodeColor));
            }
            else
            {
                setBackground(Color.WHITE);
            }
            text.setForeground(mixColor(nodeColor, Color.DARK_GRAY));
        }
        text.setBackground(getBackground());
        text.setFont(list.getFont().deriveFont(styles.get(index)));
        text.setText(value.toString());
        return text;
    }
    
    public Color mixColor(Color base, Color mix)
    {
        int r = (base.getRed() + mix.getRed())/2;
        int g = (base.getGreen() + mix.getGreen())/2;
        int b = (base.getBlue() + mix.getBlue())/2;
        
        return new Color(r,g,b);
    }
}